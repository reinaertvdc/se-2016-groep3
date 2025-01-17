package be.lambdaware.controllers;

import be.lambdaware.repos.ClassRepo;
import be.lambdaware.repos.GradeRepo;
import be.lambdaware.repos.StudentInfoRepo;
import be.lambdaware.repos.UserRepo;
import be.lambdaware.enums.ClassType;
import be.lambdaware.enums.UserRole;
import be.lambdaware.models.*;
import be.lambdaware.response.Responses;
import be.lambdaware.security.APIAuthentication;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/grade")
@CrossOrigin
public class GradeController {

    private static Logger log = Logger.getLogger(GradeController.class);
    @Autowired
    GradeRepo gradeRepo;
    @Autowired
    StudentInfoRepo studentInfoRepo;
    @Autowired
    ClassRepo classRepo;
    @Autowired
    UserRepo userRepo;

    @Autowired
    APIAuthentication authentication;

    // ===================================================================================
    // GET methods
    // ===================================================================================

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<?> getAll(@RequestHeader(name = "X-auth", defaultValue = "empty") String auth) {

        if (auth.equals("empty")) return Responses.AUTH_HEADER_EMPTY;
        if (!authentication.checkLogin(auth)) return Responses.LOGIN_INVALID;

        List<Grade> grades = gradeRepo.findAll();

        if (grades.size() == 0) return Responses.GRADES_NOT_FOUND;

        return new ResponseEntity<>(grades, HttpStatus.OK);
    }

    @RequestMapping(value = "/enabled/{enabled}", method = RequestMethod.GET)
    public ResponseEntity<?> getAllByEnabled(@RequestHeader(name = "X-auth", defaultValue = "empty") String auth, @PathVariable boolean enabled) {

        if (auth.equals("empty")) return Responses.AUTH_HEADER_EMPTY;
        if (!authentication.checkLogin(auth)) return Responses.LOGIN_INVALID;
        if (!authentication.isAdmin()) return Responses.UNAUTHORIZED;

        List<Grade> grades = gradeRepo.findAllByEnabled(enabled);

        if (grades.size() == 0) return Responses.GRADES_NOT_FOUND;
        return new ResponseEntity<>(grades, HttpStatus.OK);
    }

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@RequestHeader(name = "X-auth", defaultValue = "empty") String auth, @PathVariable long id) {

        if (auth.equals("empty")) return Responses.AUTH_HEADER_EMPTY;
        if (!authentication.checkLogin(auth)) return Responses.LOGIN_INVALID;

        Grade grade = gradeRepo.findById(id);

        if (grade == null) return Responses.GRADE_NOT_FOUND;
        return new ResponseEntity<>(grade, HttpStatus.OK);

    }

    @RequestMapping(value = "/id/{id}/name", method = RequestMethod.GET)
    public ResponseEntity<?> getNameFromGrade(@RequestHeader(name = "X-auth", defaultValue = "empty") String auth, @PathVariable long id) {

        if (auth.equals("empty")) return Responses.AUTH_HEADER_EMPTY;
        if (!authentication.checkLogin(auth)) return Responses.LOGIN_INVALID;


        Grade grade = gradeRepo.findById(id);

        if (grade == null) return Responses.GRADE_NOT_FOUND;
        return new ResponseEntity<>(grade.getName(), HttpStatus.OK);
    }

    @RequestMapping(value = "/id/{id}/enabled", method = RequestMethod.GET)
    public ResponseEntity<?> getEnabledFromGrade(@RequestHeader(name = "X-auth", defaultValue = "empty") String auth, @PathVariable long id) {

        if (auth.equals("empty")) return Responses.AUTH_HEADER_EMPTY;
        if (!authentication.checkLogin(auth)) return Responses.LOGIN_INVALID;


        Grade grade = gradeRepo.findById(id);

        if (grade == null) return Responses.GRADE_NOT_FOUND;
        return new ResponseEntity<>(grade.isEnabled(), HttpStatus.OK);
    }

    @RequestMapping(value = "/id/{id}/students", method = RequestMethod.GET)
    public ResponseEntity<?> getStudentsFromGrade(@RequestHeader(name = "X-auth", defaultValue = "empty") String auth, @PathVariable long id) {

        if (auth.equals("empty")) return Responses.AUTH_HEADER_EMPTY;
        if (!authentication.checkLogin(auth)) return Responses.LOGIN_INVALID;


        Grade grade = gradeRepo.findById(id);

        if (grade == null) return Responses.GRADE_NOT_FOUND;

        List<User> users = new ArrayList<>();

        for(StudentInfo studentInfo : grade.getStudents())
            users.add(studentInfo.getUser());

        if (users.size()==0) return Responses.USERS_NOT_FOUND;

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/id/{id}/classes", method = RequestMethod.GET)
    public ResponseEntity<?> getClassesFromGrade(@RequestHeader(name = "X-auth", defaultValue = "empty") String auth, @PathVariable long id) {

        if (auth.equals("empty")) return Responses.AUTH_HEADER_EMPTY;
        if (!authentication.checkLogin(auth)) return Responses.LOGIN_INVALID;


        Grade grade = gradeRepo.findById(id);

        if (grade == null) return Responses.GRADE_NOT_FOUND;

        List<Clazz> classes = grade.getClasses();

        if (classes.size()==0) return Responses.CLASSES_NOT_FOUND;

        return new ResponseEntity<>(classes, HttpStatus.OK);
    }

    @RequestMapping(value = "/id/{id}/objectives", method = RequestMethod.GET)
    public ResponseEntity<?> getObjectivesFromGrade(@RequestHeader(name = "X-auth", defaultValue = "empty") String auth, @PathVariable long id) {

        if (auth.equals("empty")) return Responses.AUTH_HEADER_EMPTY;
        if (!authentication.checkLogin(auth)) return Responses.LOGIN_INVALID;


        Grade grade = gradeRepo.findById(id);

        if (grade == null) return Responses.GRADE_NOT_FOUND;

        List<Objective> objectives = grade.getObjectives();

        if (objectives.size()==0) return Responses.OBJECTIVES_NOT_FOUND;

        return new ResponseEntity<>(objectives, HttpStatus.OK);
    }

    // ===================================================================================
    // PUT methods
    // ===================================================================================

    @RequestMapping(value = "/id/{id}/enable", method = RequestMethod.PUT)
    public ResponseEntity<?> enableGrade(@RequestHeader(name = "X-auth", defaultValue = "empty") String auth, @PathVariable long id) {

        if (auth.equals("empty")) return Responses.AUTH_HEADER_EMPTY;
        if (!authentication.checkLogin(auth)) return Responses.LOGIN_INVALID;
        if (!authentication.isAdmin()) return Responses.UNAUTHORIZED;

        Grade grade = gradeRepo.findById(id);

        if (grade == null) return Responses.GRADE_NOT_FOUND;

        grade.setEnabled(true);
        gradeRepo.saveAndFlush(grade);
        return Responses.GRADE_ENABLED;
    }

    @RequestMapping(value = "/id/{id}/disable", method = RequestMethod.PUT)
    public ResponseEntity<?> disableGrade(@RequestHeader(name = "X-auth", defaultValue = "empty") String auth, @PathVariable long id) {

        if (auth.equals("empty")) return Responses.AUTH_HEADER_EMPTY;
        if (!authentication.checkLogin(auth)) return Responses.LOGIN_INVALID;
        if (!authentication.isAdmin()) return Responses.UNAUTHORIZED;

        Grade grade = gradeRepo.findById(id);

        if (grade == null) return Responses.GRADE_NOT_FOUND;

        grade.setEnabled(true);
        gradeRepo.saveAndFlush(grade);
        return Responses.GRADE_DISABLED;
    }

    @RequestMapping(value = "/id/{id}/student/id/{studentId}", method = RequestMethod.PUT)
    public ResponseEntity<?> addStudentToGrade(@RequestHeader(name = "X-auth", defaultValue = "empty") String auth, @PathVariable long id,@PathVariable long studentId) {

        if (auth.equals("empty")) return Responses.AUTH_HEADER_EMPTY;
        if (!authentication.checkLogin(auth)) return Responses.LOGIN_INVALID;

        Grade grade = gradeRepo.findById(id);

        if (grade == null) return Responses.GRADE_NOT_FOUND;

        StudentInfo info = studentInfoRepo.findOne(studentId);

        if(info == null) return Responses.STUDENT_INFO_NOT_FOUND;
        User user = info.getUser();

        if(user.getRole() != UserRole.STUDENT) return  Responses.USER_NOT_STUDENT;
        List<Clazz> classes = new ArrayList<>();

        for(Clazz clazz: user.getClasses()){
            if (clazz.getType() == ClassType.PAV){
                if(clazz.getStudents().contains(user)){
                    classes.add(clazz);
                }
            }
        }

        for(Clazz clazz: classes){
            clazz.getStudents().remove(user);
            classRepo.saveAndFlush(clazz);

            user.getClasses().remove(clazz);
            userRepo.saveAndFlush(user);
        }

        info.setGrade(grade);

        studentInfoRepo.saveAndFlush(info);
        return Responses.GRADE_STUDENT_ADD;
    }

    // ===================================================================================
    // Delete methods
    // ===================================================================================

    @RequestMapping(value = "/id/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteGrade(@RequestHeader(name = "X-auth", defaultValue = "empty") String auth, @PathVariable long id) {

        if (auth.equals("empty")) return Responses.AUTH_HEADER_EMPTY;
        if (!authentication.checkLogin(auth)) return Responses.LOGIN_INVALID;
        if (!authentication.isAdmin()) return Responses.UNAUTHORIZED;

        Grade grade = gradeRepo.findById(id);

        if (grade == null) return Responses.GRADE_NOT_FOUND;

        // Remove grade from students, without deleting the student.
        for(StudentInfo student : grade.getStudents()){
            student.setGrade(null);
            studentInfoRepo.saveAndFlush(student);
        }

        // Remove grade from classes, without deleting the classes.
        for(Clazz clazz : grade.getClasses()){
            clazz.setGrade(null);
            classRepo.saveAndFlush(clazz);
        }

        gradeRepo.delete(id);
        return Responses.GRADE_DELETED;

    }

}
