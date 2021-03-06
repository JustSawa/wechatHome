package com.wechat.dao;

import com.wechat.entity.Classes;
import com.wechat.entity.ClassesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ClassesDao {
    int countByExample(ClassesExample example);

    int deleteByExample(ClassesExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Classes record);

    int insertSelective(Classes record);

    List<Classes> selectByExample(ClassesExample example);

    Classes selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Classes record, @Param("example") ClassesExample example);

    int updateByExample(@Param("record") Classes record, @Param("example") ClassesExample example);

    int updateByPrimaryKeySelective(Classes record);

    int updateByPrimaryKey(Classes record);

    Classes getClassesInfo(Integer id);


    Classes selectByTeacherId(@Param("teacherId") Integer teacherId);
}