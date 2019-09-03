package chuan.study.cloud.web.controller;

import chuan.study.cloud.pojo.domain.ApiOut;
import chuan.study.cloud.pojo.enums.ResponseCodeEnum;
import chuan.study.cloud.pojo.model.Area;
import chuan.study.cloud.pojo.vo.AreaVO;
import chuan.study.cloud.service.IAreaService;
import chuan.study.cloud.util.NumberUtils;
import chuan.study.cloud.web.annotation.AuthPolicy;
import chuan.study.cloud.web.annotation.Authenticate;
import chuan.study.cloud.web.annotation.JsonResult;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
@RestController
@RequestMapping("/v1/areas")
@Authenticate(policy = AuthPolicy.IGNORED)
@Api(value = "AreaController", tags = {"城市/地区"})
public class AreaController extends BaseController {
    private final IAreaService areaService;

    /**
     * 城市列表缓存
     */
    private static Cache<String, List<AreaVO>> areaTreeCache = Caffeine.newBuilder()
            .initialCapacity(5).maximumSize(1_000)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    @Autowired
    public AreaController(IAreaService areaService) {
        this.areaService = areaService;
    }

    /**
     * 刷新缓存
     */
    @RequestMapping(path = "/refresh", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = {RequestMethod.POST, RequestMethod.GET})
    public ApiOut<Boolean> refresh() {
        areaService.refresh();
        areaTreeCache.invalidateAll();
        return ApiOut.newSuccessResponse(true);
    }

    /**
     * 获取所有省份
     *
     * @return 所有省份
     */
    @ApiOperation(value = "显示省份列表", response = ApiOut.class)
    @GetMapping(path = "/provinces", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @JsonResult(type = AreaVO.class, exclude = {"cityCode", "zipCode", "level", "createdTime", "updatedTime"})
    public ApiOut<List<AreaVO>> getProvinces() {
        return new ApiOut.Builder<List<AreaVO>>()
                .data(areaTreeCache.get("provinces", key -> areaService.findProvinces().stream().map(this::toVo).collect(Collectors.toList())))
                .build();
    }

    /**
     * 查询指定城市的上下级
     *
     * @param areaId 的城市ID
     * @return 查询到的城市数据
     */
    @ApiOperation(value = "显示城市链", response = AreaVO.class)
    @ApiImplicitParam(name = "areaId", value = "的城市ID", required = true)
    @GetMapping(path = "/{areaId}/chain", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiOut<List<AreaVO>> getChainAreas(@PathVariable Integer areaId) {
        return new ApiOut.Builder<List<AreaVO>>().data(
                areaTreeCache.get("chain_" + areaId,
                        key -> areaService.findChain(areaId)
                                .stream().map(this::toVo)
                                .collect(Collectors.toList()))).build();
    }

    /**
     * 根据父ID查询所有下级城市
     *
     * @param parentId 的城市父ID
     * @param level    [可选] 限制查询子城市级别
     * @return 查询到的城市数据
     */
    @ApiOperation(value = "根据上级城市查询", response = AreaVO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "上级城市ID", required = true),
            @ApiImplicitParam(name = "level", value = "返回城市级数", example = "2", dataType = "int")
    })
    @GetMapping(path = "/parent/{parentId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiOut<List<AreaVO>> getAreasByParent(@PathVariable(name = "parentId") Integer parentId,
                                                 @RequestParam(name = "level", required = false) Integer level) {
        String cacheKey = "parent_" + parentId + "_" + level;
        return new ApiOut.Builder<List<AreaVO>>()
                .data(areaTreeCache.get(cacheKey, key -> toTree(areaService.findByParent(parentId, level))))
                .build();
    }

    /**
     * 根据城市区号查询
     *
     * @param cityCode 城市代码
     * @param level    [可选] 限制查询子城市级别
     * @return 查询到的城市数据
     */
    @ApiOperation(value = "根据城市区号查询", response = AreaVO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cityCode", value = "城市区号", required = true),
            @ApiImplicitParam(name = "level", value = "返回城市级数", example = "2", dataType = "int")
    })
    @GetMapping(path = "/code/{cityCode}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiOut<List<AreaVO>> getAreasByCityCode(@PathVariable String cityCode,
                                                   @RequestParam(name = "level", required = false) Integer level) {
        String cacheKey = "code_" + cityCode + "_" + level;
        return new ApiOut.Builder<List<AreaVO>>()
                .data(areaTreeCache.get(cacheKey, key -> toTree(areaService.findByCityCode(cityCode, level))))
                .build();
    }

    /**
     * 通过ids 字符串批量查询地址
     *
     * @param areaIds 城市字符串， 以","隔开
     * @return 查询到的城市数据
     */
    @ApiOperation(value = "根据的城市ID查询", response = AreaVO.class)
    @ApiImplicitParam(name = "ids", value = "的城市ID, 多个以','分隔", required = true)
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiOut<List<AreaVO>> getAreasByIds(@RequestParam("ids") Integer[] areaIds) {
        if (ArrayUtils.isEmpty(areaIds)) {
            log.warn("城市ID不能为空");
            return new ApiOut.Builder<List<AreaVO>>().code(ResponseCodeEnum.PARAMETER_REQUIRED).message("城市ID不能不空").build();
        }

        // 最多一次允许100个城市ID进行查询
        int maxAllowCount = 100;
        if (areaIds.length > maxAllowCount) {
            log.warn("传入的城市ID[{}]超过最大允许的数量[{}]", areaIds.length, maxAllowCount);
            return new ApiOut.Builder<List<AreaVO>>().code(ResponseCodeEnum.PARAMETER_RANGE_ERROR).message("城市ID太多").build();
        }

        return new ApiOut.Builder<List<AreaVO>>()
                .data(areaService.findByIds(areaIds).values().stream().map(this::toVo).collect(Collectors.toList()))
                .build();
    }


    private AreaVO toVo(Area area) {
        if (null == area) {
            return null;
        }
        return new AreaVO(area);
    }

    private List<AreaVO> toTree(List<Area> areas) {
        if (CollectionUtils.isEmpty(areas)) {
            return new ArrayList<>(0);
        }

        // 找出最小层级，并将 Area 转换成 VO Map
        int[] minLevels = {Integer.MAX_VALUE};
        Map<Integer, AreaVO> areaMap = areas.stream().map(this::toVo)
                .filter(vo -> NumberUtils.isNotNull(vo.getParentId()))
                .peek(vo -> minLevels[0] = Math.min(minLevels[0], Optional.ofNullable(vo.getLevel()).orElse(1)))
                .collect(Collectors.toMap(AreaVO::getId, area -> area));

        // 先按需排序，生成结果不再排序
        List<AreaVO> vos = new ArrayList<>(areaMap.values());
        vos.sort(Comparator.comparingInt(AreaVO::getId));

        // 组织成树的形式
        List<AreaVO> firstLevelAreas = new ArrayList<>();
        vos.forEach(area -> {
            Integer level = Optional.ofNullable(area.getLevel()).orElse(1);
            if (level > minLevels[0]) {
                Optional.ofNullable(areaMap.get(area.getParentId())).ifPresent(vo -> vo.addChild(area));
            } else if (level == minLevels[0]) {
                firstLevelAreas.add(area);
            }
        });
        return firstLevelAreas;
    }
}
