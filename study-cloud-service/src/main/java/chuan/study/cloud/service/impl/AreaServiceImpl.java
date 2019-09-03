package chuan.study.cloud.service.impl;

import chuan.study.cloud.exception.BusinessException;
import chuan.study.cloud.exception.DataNotFoundException;
import chuan.study.cloud.pojo.enums.ResponseCodeEnum;
import chuan.study.cloud.pojo.model.Area;
import chuan.study.cloud.repository.IAreaRepository;
import chuan.study.cloud.service.IAreaService;
import chuan.study.cloud.util.NumberUtils;
import chuan.study.cloud.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
@Service
public class AreaServiceImpl implements IAreaService {
    private final IAreaRepository areaRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestHighLevelClient elasticSearchClient;

    private List<Area> provinces = new ArrayList<>();
    private Map<Integer, Area> cacheByIdMap = new HashMap<>();
    private Map<Integer, List<Area>> cacheByParentMap = new HashMap<>();
    private Map<String, List<Area>> cacheByCodeMap = new HashMap<>();

    public AreaServiceImpl(IAreaRepository areaRepository, KafkaTemplate<String, String> kafkaTemplate, RestHighLevelClient elasticSearchClient) {
        this.areaRepository = areaRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.elasticSearchClient = elasticSearchClient;
    }

    @Override
    public Area load(int id) throws DataNotFoundException {
        return Optional.ofNullable(loadSafely(id))
                .orElseThrow(() -> new DataNotFoundException(ResponseCodeEnum.DATA_NOT_FOUND, "区域", id));
    }

    @Override
    public Area loadSafely(int id) {
        return cacheByIdMap.get(id);
    }

    @Override
    public List<Area> findProvinces() {
        return CollectionUtils.isEmpty(provinces) ? ImmutableList.of() : ImmutableList.copyOf(provinces);
    }

    @Override
    public Map<Integer, Area> findByIds(Integer... ids) {
        return ImmutableMap.copyOf(Arrays.stream(ids).map(this::loadSafely)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Area::getId, area -> area)));
    }

    @Override
    public List<Area> findByParent(Integer parentId, Integer level) {
        if (NumberUtils.isNotPositive(parentId)) {
            throw new BusinessException("传入的父区域ID不正确！");
        }

        List<Area> areas = cacheByParentMap.get(parentId);
        if (CollectionUtils.isEmpty(areas)) {
            return new ArrayList<>(0);
        }

        areas = new ArrayList<>(areas);
        areas.add(loadSafely(parentId));
        return filterForLevelAndSort(areas, level);
    }

    @Override
    public List<Area> findByCityCode(String cityCode, Integer level) {
        if (StringUtils.isBlank(cityCode)) {
            throw new BusinessException("传入的城市代码不正确！");
        }
        return filterForLevelAndSort(cacheByCodeMap.get(cityCode.trim()), level);
    }

    @Override
    public List<Area> findChain(Integer areaId) {
        if (NumberUtils.isNotPositive(areaId)) {
            return ImmutableList.of();
        }

        Area area;
        Integer parentId = areaId;
        int level = Integer.MAX_VALUE;
        List<Area> innerAreas = new ArrayList<>();
        while (level > 1) {
            area = loadSafely(parentId);
            if (null != area && NumberUtils.isPositive(area.getParentId())) {
                parentId = area.getParentId();
                level = area.getLevel();
                innerAreas.add(area);
            } else {
                level = 0;
            }
        }

        if (innerAreas.size() > 1) {
            innerAreas.sort(Comparator.comparingInt(Area::getLevel));
        }
        return ImmutableList.copyOf(innerAreas);
    }


    @Override
    public List<Area> findByAddress(String provinceName, String cityName, String districtName) {
        List<Area> emptyList = new ArrayList<>(0);
        if (StringUtils.isBlank(provinceName)) {
            return emptyList;
        }

        // 匹配省份
        Area province = getMatchedArea(provinceName, toAreaMap(provinces));
        if (null == province) {
            log.warn("传入的省份({})信息不正确。", provinceName);
            return new ArrayList<>(0);
        }

        // 匹配城市
        Area city = getMatchedArea(cityName, toAreaMap(cacheByParentMap.getOrDefault(province.getId(), emptyList)));
        if (null == city) {
            log.warn("传入的城市({})信息不正确。", cityName);
            return Lists.newArrayList(province);
        }

        // 匹配地区
        Area district = getMatchedArea(districtName, toAreaMap(cacheByParentMap.getOrDefault(city.getId(), emptyList)));
        if (null == district) {
            log.warn("传入的地区({})信息不正确。", districtName);
            return Lists.newArrayList(province, city);
        }

        return Lists.newArrayList(province, city, district);
    }


    @Override
    @Async("taskExecutor")
    public void refresh() {
        this.initialize();
        log.info("=== 同步城市数据到 ES | 开始 === ");

        String indexName = "area";
        cacheByIdMap.forEach((k, v) -> {
            try {
                String jsonString = JSON.toJSONString(v);
                UpdateRequest request = new UpdateRequest(indexName, String.valueOf(k))
                        .doc(jsonString, XContentType.JSON)
                        .upsert(jsonString, XContentType.JSON);

                elasticSearchClient.update(request, RequestOptions.DEFAULT);
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
        });
        log.info("=== 同步城市数据到 ES | 结束 === ");
        // kafkaTemplate.send(Constants.KAFKA_TOPIC_NAME, String.valueOf(SystemClock.now()), "area-refreshed");
    }

    /** ========================================== private method =============================== */
    /**
     * 查询匹配的城市
     *
     * @param name    城市名称
     * @param areaMap 按首字母分组的城市列表
     * @return 匹配的城市
     */
    private Area getMatchedArea(final String name, final Map<String, List<Area>> areaMap) {
        if (StringUtils.isBlank(name) || MapUtils.isEmpty(areaMap)) {
            return null;
        }

        String initial = StringUtils.chineseToPinyin(name, true);
        if (StringUtils.isBlank(initial)) {
            return null;
        }

        List<Area> areas = areaMap.getOrDefault(String.valueOf(initial.charAt(0)).toUpperCase(), new ArrayList<>(0));
        for (Area area : areas) {
            if (StringUtils.getSimilarityRatio(area.getName(), name) > 0.5) {
                return area;
            }
        }
        return null;
    }


    private Map<String, List<Area>> toAreaMap(List<Area> areas) {
        Map<String, List<Area>> areaMap = new HashMap<>(areas.size());

        areas.forEach(area -> {
            String initial1 = StringUtils.upperCase(area.getInitial());
            String initial2 = String.valueOf(StringUtils.chineseToPinyin(area.getName(), true).charAt(0)).toUpperCase();

            List<Area> innerAreas = areaMap.computeIfAbsent(initial1, k -> new ArrayList<>());
            innerAreas.add(area);

            // 如果是多音字
            if (!StringUtils.equals(initial1, initial2)) {
                List<Area> innerAreas2 = areaMap.computeIfAbsent(initial2, k -> new ArrayList<>());
                innerAreas2.add(area);
            }
        });

        return areaMap;
    }


    /**
     * 按城市所在级别进行过滤
     *
     * @param areas 城市列表
     * @param level 城市级别
     * @return 过滤和排序后的城市列表
     */
    private List<Area> filterForLevelAndSort(List<Area> areas, Integer level) {
        if (CollectionUtils.isEmpty(areas)) {
            return new ArrayList<>(0);
        }

        List<Area> innerAreas;

        // 根据城市级别进行过滤
        if (NumberUtils.isPositive(level)) {
            innerAreas = areas.stream().filter(area -> level.equals(area.getLevel())).collect(Collectors.toList());
        } else {
            innerAreas = new ArrayList<>(areas);
        }

        // 按序号排序
        if (CollectionUtils.isNotEmpty(innerAreas)) {
            innerAreas.sort(Comparator.comparingInt(Area::getId));
        }
        return ImmutableList.copyOf(innerAreas);
    }

    @PostConstruct
    private void initialize() {
        long startTime = System.currentTimeMillis();

        LambdaQueryWrapper<Area> queryWrapper = new LambdaQueryWrapper<Area>()
                .select(Area.class, c -> !StringUtils.endsWithIgnoreCase(c.getProperty(), "time"));
        Optional.ofNullable(areaRepository.selectList(queryWrapper)).ifPresent(areas -> {
            cacheByIdMap = areas.stream().collect(Collectors.toMap(Area::getId, area -> area));
            provinces = ImmutableList.copyOf(areas.stream()
                    .filter(area -> null != area.getLevel() && 1 == area.getLevel())
                    .sorted(Comparator.comparing(Area::getId))
                    .collect(Collectors.toList()));

            cacheByParentMap = areas.stream()
                    .filter(area -> NumberUtils.isPositive(area.getParentId()))
                    .collect(Collectors.groupingBy(Area::getParentId));

            cacheByCodeMap = areas.stream()
                    .filter(area -> StringUtils.isNotBlank(area.getCityCode()))
                    .collect(Collectors.groupingBy(Area::getCityCode));
        });
        log.info("获取{}条城市数据，耗时{}毫秒", cacheByIdMap.size(), System.currentTimeMillis() - startTime);
    }
}