package org.ye.psys.db.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.ye.psys.db.entity.Storage;
import org.ye.psys.db.entity.StorageExample;

public interface StorageMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     */
    long countByExample(StorageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     */
    int deleteByExample(StorageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     */
    int insert(Storage record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     */
    int insertSelective(Storage record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    Storage selectOneByExample(StorageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    Storage selectOneByExampleSelective(@Param("example") StorageExample example, @Param("selective") Storage.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<Storage> selectByExampleSelective(@Param("example") StorageExample example, @Param("selective") Storage.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     */
    List<Storage> selectByExample(StorageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    Storage selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") Storage.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     */
    Storage selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    Storage selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") Storage record, @Param("example") StorageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") Storage record, @Param("example") StorageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Storage record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Storage record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByExample(@Param("example") StorageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table storage
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByPrimaryKey(Integer id);
}