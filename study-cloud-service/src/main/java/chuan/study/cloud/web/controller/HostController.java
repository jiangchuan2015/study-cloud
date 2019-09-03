package chuan.study.cloud.web.controller;

import chuan.study.cloud.pojo.domain.ApiOut;
import chuan.study.cloud.pojo.model.Area;
import chuan.study.cloud.pojo.vo.AreaVO;
import chuan.study.cloud.service.IAreaService;
import chuan.study.cloud.util.StringUtils;
import chuan.study.cloud.web.annotation.AuthPolicy;
import chuan.study.cloud.web.annotation.Authenticate;
import chuan.study.cloud.web.annotation.JsonResult;
import com.github.jarod.qqwry.IPZone;
import com.github.jarod.qqwry.QQWry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-09-03
 */
@Slf4j
@RestController
@RequestMapping("/v1/hosts")
@Authenticate(policy = AuthPolicy.IGNORED)
@Api(value = "HostController", tags = {"IP/城市转换"})
public class HostController extends BaseController {
    private final QQWry qqWry;
    private final IAreaService areaService;

    @Autowired
    public HostController(QQWry qqWry, IAreaService areaService) {
        this.qqWry = qqWry;
        this.areaService = areaService;
    }

    @ApiOperation(value = "请求者IP", response = ApiOut.class)
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiOut<String> getRequestIp() {
        return ApiOut.newSuccessResponse(getRequestHost(httpRequest));
    }

    @ApiOperation(value = "将指定IP转城市", response = ApiOut.class)
    @GetMapping(path = "/{ip}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @JsonResult(type = AreaVO.class, exclude = {"createdTime", "updatedTime"})
    public ApiOut<List<AreaVO>> getRequestCity(@PathVariable("ip") String ip) {
        IPZone ipZone = qqWry.findIP(ip);
        if (Objects.isNull(ipZone)) {
            return ApiOut.newParameterRequiredResponse("IP地址不正确");
        }

        String cityName = ipZone.getMainInfo();
        log.info("IP:{}, 地址:{}", ip, ipZone);
        String province = "", city = "", district = "";

        // 获取省份
        int provinceIndex = cityName.indexOf("省") + 1;
        if (provinceIndex > 0) {
            province = cityName.substring(0, provinceIndex);
            cityName = cityName.substring(provinceIndex);
        }

        // 如果没取到省，可能是直辖市
        if (StringUtils.isBlank(province)) {
            provinceIndex = cityName.indexOf("市") + 1;
            if (provinceIndex > 0) {
                province = cityName.substring(0, provinceIndex);
                city = province;
                cityName = cityName.substring(provinceIndex);
            }

        }

        // 获取市
        int cityIndex = cityName.indexOf("市") + 1;
        if (cityIndex > 0) {
            city = cityName.substring(0, cityIndex);
            cityName = cityName.substring(cityIndex);
        }

        // 获取区
        int districtIndex = cityName.indexOf("区") + 1;
        if (districtIndex > 0) {
            district = cityName.substring(0, districtIndex);
        }

        List<Area> areas = areaService.findByAddress(province, city, district);
        return ApiOut.newSuccessResponse(areas.stream().map(AreaVO::new).collect(Collectors.toList()));
    }
}
