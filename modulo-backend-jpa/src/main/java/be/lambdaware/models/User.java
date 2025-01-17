package be.lambdaware.models;

import be.lambdaware.enums.Sex;
import be.lambdaware.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    // ===================================================================================
    // Fields
    // ===================================================================================

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 128)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    private boolean enabled = true;

    // ===================================================================================
    // Relations
    // ===================================================================================

    @OneToMany(mappedBy = "teacher")
    @JsonIgnore
    private List<Clazz> teachedClasses = new ArrayList<>();

    @ManyToMany(mappedBy = "students")
    @JsonIgnore
    private List<Clazz> classes = new ArrayList<>();

    @OneToOne(mappedBy = "user", orphanRemoval = true)
    @JsonManagedReference
    private StudentInfo studentInfo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User parent;

    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    private List<User> children = new ArrayList<>();

    @ManyToMany(mappedBy = "students")
    @JsonIgnore
    private List<CourseTopic> courseTopics = new ArrayList<>();

    // ===================================================================================

    public User() {
    }

    public User(String email, String password, String firstName, String lastName, Sex sex, UserRole role, boolean enabled) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sex = sex;
        this.role = role;
        this.enabled = enabled;
    }

    // ===================================================================================
    // Field Accessors
    // ===================================================================================

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // ===================================================================================
    // Relation Accesors
    // ===================================================================================

    public List<Clazz> getTeachedClasses() {
        return teachedClasses;
    }

    public void setTeachedClasses(List<Clazz> teachedClasses) {
        this.teachedClasses = teachedClasses;
    }

    public void addTeachedClass(Clazz clazz) {
        this.teachedClasses.add(clazz);
        if (clazz.getTeacher() != this) {
            clazz.setTeacher(this);
        }
    }

    public List<Clazz> getClasses() {
        return classes;
    }

    public void setClasses(List<Clazz> classes) {
        this.classes = classes;
    }

    public void addClass(Clazz clazz) {
        this.classes.add(clazz);
        if (!clazz.getStudents().contains(this)) {
            clazz.getStudents().add(this);
        }
    }

    public StudentInfo getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(StudentInfo studentInfo) {
        this.studentInfo = studentInfo;
        if (studentInfo.getUser() != this) {
            studentInfo.setUser(this);
        }
    }

    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
        if (parent != null && !parent.getChildren().contains(this)) {
            parent.getChildren().add(this);
        }
    }

    public List<User> getChildren() {
        return children;
    }

    public void setChildren(List<User> children) {
        this.children = children;
    }

    public void addChild(User user) {
        this.children.add(user);
        if (user.getParent() != this) {
            user.setParent(this);
        }
    }

    public List<CourseTopic> getCourseTopics() {
        return courseTopics;
    }

    public void setCourseTopics(List<CourseTopic> courseTopics) {
        this.courseTopics = courseTopics;
    }

    public void addCourseTopic(CourseTopic courseTopic) {
        this.courseTopics.add(courseTopic);
        if (!courseTopic.getStudents().contains(this)) {
            courseTopic.getStudents().add(this);
        }
    }

    // ===================================================================================


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (enabled != user.enabled) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
        if (sex != user.sex) return false;
        if (role != user.role) return false;
        if (teachedClasses != null ? !teachedClasses.equals(user.teachedClasses) : user.teachedClasses != null)
            return false;
        if (classes != null ? !classes.equals(user.classes) : user.classes != null) return false;
        if (studentInfo != null ? !studentInfo.equals(user.studentInfo) : user.studentInfo != null) return false;
        if (parent != null ? !parent.equals(user.parent) : user.parent != null) return false;
        if (children != null ? !children.equals(user.children) : user.children != null) return false;
        return courseTopics != null ? courseTopics.equals(user.courseTopics) : user.courseTopics == null;

    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", sex=" + sex +
                ", role=" + role +
                ", enabled=" + enabled +
                '}';
    }
}