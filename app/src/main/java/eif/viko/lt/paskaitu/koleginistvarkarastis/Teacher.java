package eif.viko.lt.paskaitu.koleginistvarkarastis;

public class Teacher {
    private String subject;
    private String group;
    private String classroom;
    private String starttime;
    private String teacher;
    private String endtime;
    private String uniperiod;


    public Teacher(String subject, String group, String classroom, String starttime, String teacher, String endtime, String uniperiod) {
        this.subject = subject;
        this.group = group;
        this.classroom = classroom;
        this.starttime = starttime;
        this.teacher = teacher;
        this.endtime = endtime;
        this.uniperiod = uniperiod;
    }

    public Teacher() {
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getUniperiod() {
        return uniperiod;
    }

    public void setUniperiod(String uniperiod) {
        this.uniperiod = uniperiod;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "subject='" + subject + '\'' +
                ", group='" + group + '\'' +
                ", classroom='" + classroom + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", uniperiod='" + uniperiod + '\'' +
                '}';
    }
}
