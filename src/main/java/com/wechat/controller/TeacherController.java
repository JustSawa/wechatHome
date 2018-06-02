package com.wechat.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.Page;
import com.wechat.bean.MyClass;
import com.wechat.bean.TableResult;
import com.wechat.bean.TeacherHomeworkResult;
import com.wechat.common.controller.BaseController;
import com.wechat.dao.ClassesDao;
import com.wechat.dao.ExamResultDao;
import com.wechat.dao.LeaveRecordDao;
import com.wechat.dao.NoticeBulletinDao;
import com.wechat.entity.*;
import com.wechat.mapper.ClassesMapper;
import com.wechat.mapper.TeacherClassMapper;
import com.wechat.model.CommonResult;
import com.wechat.model.NoticeTemplateQueue;
import com.wechat.service.StudentService;
import com.wechat.service.TeacherService;
import com.wechat.util.DateUtil;
import com.wechat.util.OfficeUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teacher")
@Transactional
public class TeacherController extends BaseController {


    @Autowired
    private ClassesMapper classesMapper;
    @Autowired
    private TeacherClassMapper teacherClassMapper;
    @Autowired
    private ClassesDao classesDao;
    @Autowired
    private NoticeBulletinDao noticeDao;
    @Autowired
    StudentService studentService;
    @Autowired
    TeacherService teacherService;


    @PostMapping("/userLogin")
    public CommonResult<Object> teacherLogin(HttpServletRequest request,Teacher teacher){
        List<Teacher> list = teacher.selectList("teacher_id = {0} and password = {1}",teacher.getTeacherId(),teacher.getPassword());
        if(list.size() != 0){
            request.getSession().setAttribute("user",list.get(0));

            return new CommonResult<>(successcode,list.get(0));
        }
        return new CommonResult<>(errorcode,"用户名或者密码错误");
    }


    @ApiOperation(value = "新建班级信息接口",notes = "",produces = "application/json")
    @ApiImplicitParam(name = "classes",
                    value = "需要添加的班级实体Classes",
                    required = true, dataType = "Classes")
    @RequestMapping(value = "/addClasses",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public CommonResult<Object> addClasses(@RequestBody Classes classes){
        if (classesMapper.insert(classes) == 1){
            return new CommonResult<>(successcode,successMessage);
        }
        return  new CommonResult<>(errorcode,errorMessage);
    }


    @ApiOperation(value = "获取班级概况信息接口",notes = "",produces = "application/json")
    @ApiImplicitParam(name = "id",
            value = "教师id",
            required = true, dataType = "Integer")
    @GetMapping(value = "/getClassesInfo/{id}",produces = "application/json")
    public CommonResult<Object> getClassesInfo(@PathVariable("id") Integer id){
        //根据传入的教师id
        MyClass myClass = new MyClass();

        Classes classes = new Classes();
        Teacher teacher = new Teacher();
        Student student = new Student();
        TeacherClass teacherClass = new TeacherClass();
        EntityWrapper<Classes> ew = new EntityWrapper<>();
        ew.where("teacher = {0}",id);
        List<Classes> classesList = classes.selectList(ew);
        if(classesList.size()!= 0){
            //设置该教师的班级信息
            myClass.setMyclass(classesList.get(0));
            teacher = teacher.selectById(myClass.getMyclass().getTeacher());
            //设置该班班主任
            myClass.setHeadMaster(teacher);
            //设置该班学生
            myClass.setStudentList(student.selectList("cla_id = {0}",myClass.getMyclass().getId()));

            //封装该班教师
            List<Teacher> teachers = new ArrayList<>();
            List<TeacherClass> teacherClasses = teacherClass.selectList("cla_id = {0}",myClass.getMyclass().getId());
            for(TeacherClass t : teacherClasses){
                teachers.add(teacher.selectById(t.getTeaId()));
            }
            myClass.setTeacherList(teachers);

            return new CommonResult<>(successcode,myClass);
        }

        return new CommonResult<>(errorcode,errorMessage);
    }

    //批量上传学生接口
    @PostMapping("uploadStudent")
    public CommonResult<String> uploadStudent(MultipartFile file, HttpServletRequest request){
        if(file == null && file.isEmpty()){
            return new CommonResult(errorcode,"the file is empty");
        }
        InputStream in = null;

        try {
            in = file.getInputStream();
        } catch (IOException e) {
            return new CommonResult(errorcode,"get the InputStream is error");
        }

        Teacher teacher = (Teacher)request.getSession().getAttribute("user");
        Classes classes = teacherService.getClassByTeacherId(teacher);
        List<Student> list = OfficeUtil.readStudent(in,1,classes.getId());
        for(Student student:list){
            student.insert();
        }
        return new CommonResult(0,"batch regist is susccess");
    }

    //批量上传成绩接口
    @PostMapping("/uploadStudentScore")
    public CommonResult<String> uploadStudentScore(MultipartFile file,HttpServletRequest request){
        if(file == null && file.isEmpty()){
            return new CommonResult(errorcode,"the file is empty");
        }
        InputStream in = null;

        try {
            in = file.getInputStream();
        } catch (IOException e) {
            return new CommonResult(errorcode,"get the InputStream is error");
        }
        List<ExamResult> list = OfficeUtil.readStudentScore(in,1,studentService);
        for (ExamResult result:list){
            result.insert();
        }

        return new CommonResult(0,"batch add examscore is susccess");
    }

    //获取教师信息接口
    @GetMapping("getTeacher/{id}")
    public CommonResult<Teacher> getTeacher(@PathVariable int id){
        Teacher teacher = new Teacher();
        teacher.selectById(id);
        return new CommonResult<Teacher>(0,teacher);
    }
    //发布作业接口
    @RequestMapping("/addHomework")
    public CommonResult<String> addHomework(HttpServletRequest request,int claId,String title,String content,String finshTime){
        Teacher teacher = (Teacher)request.getSession().getAttribute("user");
        Homework homework = new Homework();
        homework.setClaId(claId);
        homework.setTitle(title);
        homework.setContent(content);
        homework.setTeaId(teacher.getId());
        homework.setFinshTime(DateUtil.StringtoDate(finshTime,"yyyy-MM-dd"));
        homework.insert();

        return new CommonResult(0,"success");
    }
    //根据教师id获取所有作业
    @RequestMapping("/getHomeworkByTid")
    public TableResult<Object> getHomeworkByTid(HttpServletRequest request,@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int limit){
        Teacher teacher = (Teacher)request.getSession().getAttribute("user");
        TableResult<Object> result = new TableResult<>();
        Page<Map<String,Object>> mapList = teacherService.getHomework(teacher.getId(),page,limit);

        result.setCount((int)mapList.getTotal());
        result.setData(mapList);
        result.setCode(0);

        return result;
    }

    //发布通知接口
    @PostMapping("/addNotice")
    public CommonResult<String> addNotice(HttpServletRequest request,NoticeBulletin notice){
        //Teacher teacher = (Teacher)request.getSession().getAttribute("user");

        //notice.setTeaId(teacher.getId());
        notice.insert();

        NoticeTemplateQueue.getInstance().addTemplate(notice);

        return new CommonResult(0,successMessage);
    }

    //发布作业时需要的班级列表
    @RequestMapping("/getMyclassList")
    public CommonResult<Object> getMyclassList(HttpServletRequest request){
        Teacher teacher = (Teacher)request.getSession().getAttribute("user");
        TeacherClass teacherClass = new TeacherClass();
        List<Classes> classesList = new ArrayList<>();
        Classes classes = new Classes();
        List<TeacherClass> list = teacherClass.selectList("tea_id = {0}",teacher.getId());
        for(TeacherClass tc : list){
            classesList.add(classes.selectById(tc.getClaId()));
        }
        return new CommonResult(successcode,classesList);
    }

    //获取所有通知
    @RequestMapping("/getAllNotice")
    public TableResult<Object> getAllNotice(HttpServletRequest request, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int limit){
        TableResult<Object> result = new TableResult<>();
        result.setCode(0);
        result.setMsg("操作成功");
        Page<Map<String,Object>> mapPage = teacherService.getNotice(page,limit);
        NoticeBulletin notice = new NoticeBulletin();
        List<NoticeBulletin> list = notice.selectAll();
        result.setCount(list.size());
        result.setData(mapPage);
        return result;
    }
    //根据日期范围查询通知
    @RequestMapping("/getNoticeByTime")
    public CommonResult<Object> getNoticeByTime(HttpServletRequest request,String startTime,String endTime){
        List<Map<String,Object>> result = noticeDao.getAllNoticeByTime(startTime,endTime);
        return new CommonResult<>(successcode,result);
    }

    //上传单个学生成绩
    @RequestMapping("/addExamResult")
    public CommonResult<Object> addExamResult(HttpServletRequest request,ExamResult examResult){
        if(examResult.insert()){
            return new CommonResult<>(successcode,successMessage);
        }else{
            return new CommonResult<>(errorcode,errorMessage);
        }
    }

    //教师端获取请假记录
    @RequestMapping("/getLeaveRecord")
    public TableResult<Object> getLeaveRecord(HttpServletRequest request,@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int limit){
        Teacher teacher = (Teacher)request.getSession().getAttribute("user");
        TableResult<Object> result = new TableResult<>();
        Page<Map<String,Object>> mapList = teacherService.getLeaverRecord(teacher.getId(),page,limit);
        result.setCode(0);
        result.setCount((int)mapList.getTotal());
        result.setData(mapList);
        return result;
    }
    //所有成绩
    @RequestMapping("/getExamResult")
    public TableResult<Object> getExamResult(HttpServletRequest request,@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int limit){
        Teacher teacher = (Teacher)request.getSession().getAttribute("user");
        TableResult<Object> result = new TableResult<>();
        Page<Map<String,Object>> mapList = teacherService.getExamResult(teacher.getId(),page,limit);

        result.setCount((int)mapList.getTotal());
        result.setData(mapList);
        result.setCode(0);

        return result;
    }

}
