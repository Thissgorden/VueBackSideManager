package com.gdlearn.backsidemanager.mapper;

import com.gdlearn.backsidemanager.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2021-10-27
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    //@Select("SELECT DISTINCT menu_id FROM sys_user_role SUR LEFT JOIN sys_role_menu rm ON SUR.role_id WHERE SUR.user_Id =#{userId} ;")
    @Select("SELECT DISTINCT menu_id FROM sys_user_role sur LEFT JOIN sys_role_menu srm ON sur.`role_id` WHERE sur.user_id =#{userId} AND sur.`role_id`=srm.role_id;")
    List<Long> getNavMenuIdsd(Long userId);

    @Select("SELECT \n" +
            "DISTINCT SU.*\n" +
            "FROM sys_user_role SUR \n" +
            "LEFT JOIN sys_role_menu RM ON SUR.role_id\n" +
            "LEFT JOIN sys_user SU ON SUR.`user_id` = SU.`id`\n" +
            "WHERE menu_id = #{menuId};")
    List<SysUser> listByMenuId(Long menuId);
}
