package chuan.study.cloud.service;

import chuan.study.cloud.exception.DataNotFoundException;
import chuan.study.cloud.pojo.model.Area;

import java.util.List;
import java.util.Map;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public interface IAreaService {
    /**
     * 查询区域详情
     *
     * @param id PK
     * @return 区域详情
     * @throws DataNotFoundException 如果没有找到数据，将会抛出异常
     */
    Area load(int id) throws DataNotFoundException;

    /**
     * 查询区域详情
     *
     * @param id PK
     * @return 区域详情
     */
    Area loadSafely(int id);


    /**
     * 查询所有省，直辖市数据
     *
     * @return 查询所有省份数据
     */
    List<Area> findProvinces();

    /**
     * 根据区域IDs查询数据
     *
     * @param ids 区域IDs
     * @return 满足条件的区域数据
     */
    Map<Integer, Area> findByIds(Integer... ids);

    /**
     * 查询下级区域
     *
     * @param parentId 上级ID
     * @param level    返回第几级数据
     * @return 满足条件的区域数据
     */
    List<Area> findByParent(Integer parentId, Integer level);

    /**
     * 根据区号查询数据
     *
     * @param cityCode 区号
     * @param level    返回第几级数据
     * @return 满足条件的区域数据
     */
    List<Area> findByCityCode(String cityCode, Integer level);

    /**
     * 通过ID持续向上查找，直到最父级
     *
     * @param areaId 区域ID
     * @return 从当前节点出发，直接到无父级为止
     */
    List<Area> findChain(Integer areaId);

    /**
     * 根据省市区查询详情
     *
     * @param province 省
     * @param city     市
     * @param district 区
     * @return
     */
    List<Area> findByAddress(String province, String city, String district);

    /**
     * 从数据库中重新加载数据
     *
     * @return true: 重加载成功
     */
    void refresh();
}