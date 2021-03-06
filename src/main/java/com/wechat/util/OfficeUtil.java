package com.wechat.util;

import com.wechat.entity.ExamResult;
import com.wechat.entity.Student;
import com.wechat.model.SubjectEnum;
import com.wechat.service.StudentService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by zxc on 2018/5/31.
 */
public class OfficeUtil {


	//读取学生个人信息
	public static List<Student> readStudent(InputStream inputStream, int ingoreRow,int cla_id){
		List<Student> list = new ArrayList<>();
		if(inputStream == null){
			return list;
		}
		Workbook wb = null;
		try {
			wb = WorkbookFactory.create(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		Cell cell = null;
		//遍历sheet
		for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
			//获取当前sheet
			Sheet st = wb.getSheetAt(sheetIndex);
			if(st == null){
				continue;
			}
			//遍历当前sheet的row
			// 第一行为标题，不取
			for (int rowIndex = ingoreRow; rowIndex <= st.getLastRowNum(); rowIndex++) {
				Row row = st.getRow(rowIndex);
				if (row == null) {
					continue;
				}
				Student student = new Student();
				student.setClaId(cla_id);
				student.setPassword("123456");
				boolean haveValue = false;
				for (int columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
					cell = row.getCell(columnIndex);
					if (cell != null) {
						haveValue = true;
						switch (columnIndex) {
							case 0:
								student.setName(cell.getStringCellValue());
								break;
							case 1:
								student.setSex(cell.getStringCellValue());
								break;
							case 2:
								student.setIdentityNumber(cell.getStringCellValue());
								break;
							case 3:
								String studentNumber = new  DecimalFormat("0").format(cell.getNumericCellValue());
								student.setStudentNumber(studentNumber);
								break;
							default:
								break;
						}
					}
				}
				if(haveValue){
					list.add(student);
				}
			}
		}
		return list;
	}

	public static List<ExamResult> readStudentScore(InputStream inputStream, int ingoreRow, StudentService studentService,boolean isExcel2003){
		List<ExamResult> list = new ArrayList<>();
		if(inputStream == null){
			return list;
		}
		Workbook wb = null;
		try {
			wb = WorkbookFactory.create(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		Cell cell = null;
		//遍历sheet
		for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
			//获取当前sheet
			Sheet st = wb.getSheetAt(sheetIndex);
			if(st == null){
				continue;
			}
			//遍历当前sheet的row
			// 第一行为标题，不取
			for (int rowIndex = ingoreRow; rowIndex <= st.getLastRowNum(); rowIndex++) {
				Row row = st.getRow(rowIndex);
				if (row == null) {
					continue;
				}
				cell = row.getCell(0);
				if(cell == null){
					continue;
				}
				String studentNumber = new  DecimalFormat("0").format(cell.getNumericCellValue());
				Student student = studentService.getStudentInfoByStudentNumber(studentNumber);
				ExamResult result = new ExamResult();
				result.setStuId(student.getId());

				boolean haveValue = false;
				for (int columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
					cell = row.getCell(columnIndex);
					if (cell != null) {
						haveValue = true;
						switch (columnIndex) {
							case 1:
								result.setSubject(SubjectEnum.getCodeByName(cell.getStringCellValue()));
								break;
							case 2:
								result.setScore((float)cell.getNumericCellValue());
								break;
							case 3:
								result.setTerm((int)cell.getNumericCellValue());
								break;
							default:
								break;
						}
					}
				}

				if(haveValue){
					list.add(result);
				}
			}
		}
		return list;
	}
}
