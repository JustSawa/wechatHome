package com.wechat.dao;

import com.wechat.entity.Homework;
import com.wechat.entity.HomeworkExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HomeworkMapper {
    long countByExample(HomeworkExample example);

    int deleteByExample(HomeworkExample example);

    int deleteByPrimaryKey(Integer h_id);

    int insert(Homework record);

    int insertSelective(Homework record);

    List<Homework> selectByExampleWithBLOBs(HomeworkExample example);

    List<Homework> selectByExample(HomeworkExample example);

    Homework selectByPrimaryKey(Integer h_id);

    int updateByExampleSelective(@Param("record") Homework record, @Param("example") HomeworkExample example);

    int updateByExampleWithBLOBs(@Param("record") Homework record, @Param("example") HomeworkExample example);

    int updateByExample(@Param("record") Homework record, @Param("example") HomeworkExample example);

    int updateByPrimaryKeySelective(Homework record);

    int updateByPrimaryKeyWithBLOBs(Homework record);

    int updateByPrimaryKey(Homework record);
}