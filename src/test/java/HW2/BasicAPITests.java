
package HW2;

import HW2.business.*;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class BasicAPITests extends AbstractTest {
    @org.junit.Test
    public void studentAttendTest() {

        ReturnValue res;
        Test s = new Test();
        s.setId(1);
        s.setSemester(1);
        s.setRoom(233);
        s.setDay(1);
        s.setTime(1);
        s.setCreditPoints(3);


        res = Solution.addTest(s);
        assertEquals(ReturnValue.OK, res);

        Student a = new Student();
        a.setId(2);
        a.setName("Roei");
        a.setCreditPoints(117);
        a.setFaculty("CS");
        ReturnValue ret = Solution.addStudent(a);
        assertEquals(ReturnValue.OK, ret);

        res = Solution.studentAttendTest(2, s.getId(), s.getSemester());
        assertEquals(ReturnValue.OK, res);

        res = Solution.studentAttendTest(1,  s.getId(), s.getSemester());
        assertEquals(ReturnValue.NOT_EXISTS, res);


    }

    @org.junit.Test
    public void studentGraduateTest() {
        ReturnValue res;
        Test s = new Test();
        s.setId(1);
        s.setSemester(1);
        s.setRoom(233);
        s.setDay(1);
        s.setTime(1);
        s.setCreditPoints(3);

        Test s2 = new Test();
        s2.setId(2);
        s2.setSemester(1);
        s2.setRoom(233);
        s2.setDay(1);
        s2.setTime(1);
        s2.setCreditPoints(3);

        Test s3 = new Test();
        s3.setId(2);
        s3.setSemester(3);
        s3.setRoom(233);
        s3.setDay(2);
        s3.setTime(1);
        s3.setCreditPoints(3);

        Test s4 = new Test();
        s4.setId(2);
        s4.setSemester(2);
        s4.setRoom(233);
        s4.setDay(1);
        s4.setTime(1);
        s4.setCreditPoints(3);

        res = Solution.addTest(s);
        assertEquals(ReturnValue.OK, res);

        res = Solution.addTest(s2);
        assertEquals(ReturnValue.OK, res);

        res = Solution.addTest(s3);
        assertEquals(ReturnValue.OK, res);

        res = Solution.addTest(s4);
        assertEquals(ReturnValue.OK, res);

        Test s1 = new Test();
        s1.setId(1);
        s1.setSemester(2);
        s1.setRoom(233);
        s1.setDay(1);
        s1.setTime(1);
        s1.setCreditPoints(3);
        res = Solution.addTest(s1);
        assertEquals(ReturnValue.OK, res);

        Test s5 = new Test();
        s5.setId(3);
        s5.setSemester(2);
        s5.setRoom(233);
        s5.setDay(1);
        s5.setTime(1);
        s5.setCreditPoints(3);
        res = Solution.addTest(s5);
        assertEquals(ReturnValue.OK, res);


        Student a = new Student();
        a.setId(2);
        a.setName("Roei");
        a.setCreditPoints(117);
        a.setFaculty("CS");
        ReturnValue ret = Solution.addStudent(a);
        assertEquals(ReturnValue.OK, ret);

        Student a2 = new Student();
        a2.setId(3);
        a2.setName("Ben");
        a2.setCreditPoints(100);
        a2.setFaculty("CS");
        ret = Solution.addStudent(a2);
        assertEquals(ReturnValue.OK, ret);

        res = Solution.studentAttendTest(2,  s.getId(), s.getSemester());
        assertEquals(ReturnValue.OK, res);
        res = Solution.studentAttendTest(2,  s2.getId(), s2.getSemester());
        assertEquals(ReturnValue.OK, res);
        res = Solution.studentAttendTest(2,  s3.getId(), s3.getSemester());
        assertEquals(ReturnValue.OK, res);
        res = Solution.studentAttendTest(2,  s4.getId(), s4.getSemester());
        assertEquals(ReturnValue.OK, res);
        res = Solution.studentAttendTest(2,  s1.getId(), s1.getSemester());
        assertEquals(ReturnValue.OK, res);

        res = Solution.studentAttendTest(3,  s.getId(), s.getSemester());
        assertEquals(ReturnValue.OK, res);
        res = Solution.studentAttendTest(3,  s2.getId(), s2.getSemester());
        assertEquals(ReturnValue.OK, res);
        res = Solution.studentAttendTest(3,  s3.getId(), s3.getSemester());
        assertEquals(ReturnValue.OK, res);

//        Boolean half = Solution.studentHalfWayThere(a.getId());
//        assertEquals(Boolean.TRUE, half);

        Supervisor sup = new Supervisor();
        sup.setId(1);
        sup.setName("");
        sup.setSalary(30);

        Supervisor sup2 = new Supervisor();
        sup2.setId(2);
        sup2.setName("");
        sup2.setSalary(20);

        res = Solution.addSupervisor(sup);
        assertEquals(ReturnValue.OK, res);


        res = Solution.addSupervisor(sup2);
        assertEquals(ReturnValue.OK, res);

        res = Solution.supervisorOverseeTest(1,  s.getId(), s.getSemester());
        assertEquals(ReturnValue.OK, res);

        ArrayList<Integer> arr = Solution.supervisorOverseeStudent();
        assertEquals(0, arr.size());

        res = Solution.supervisorOverseeTest(1,  s1.getId(), s1.getSemester());
        assertEquals(ReturnValue.OK, res);





        res = Solution.supervisorOverseeTest(1,  s2.getId(), s2.getSemester());
        assertEquals(ReturnValue.OK, res);

        res = Solution.supervisorOverseeTest(1,  s3.getId(), s3.getSemester());
        assertEquals(ReturnValue.OK, res);

        res = Solution.supervisorOverseeTest(1,  s4.getId(), s4.getSemester());
        assertEquals(ReturnValue.OK, res);

        res = Solution.supervisorOverseeTest(1,  s5.getId(), s5.getSemester());
        assertEquals(ReturnValue.OK, res);

        res = Solution.supervisorOverseeTest(2,  s4.getId(), s4.getSemester());
        assertEquals(ReturnValue.OK, res);

        res = Solution.supervisorOverseeTest(2,  s5.getId(), s5.getSemester());
        assertEquals(ReturnValue.OK, res);


//        arr = Solution.supervisorOverseeStudent();
//        assertEquals(Integer.valueOf(2), arr.get(0));
//        assertEquals(1, arr.size());

        Integer points = Solution.studentCreditPoints(a.getId());
        assertEquals(Integer.valueOf(132), points);

        Integer test = Solution.getMostPopularTest(a.getFaculty());
        assertEquals(Integer.valueOf(2), test);

        Float average_cost = Solution.averageTestCost();
        assertEquals(Float.valueOf(28.333334f), average_cost);

        arr = Solution.getCloseStudents(2);
        assertEquals(Integer.valueOf(3), arr.get(0));
        assertEquals(1, arr.size());

        arr = Solution.getConflictingTests();
        assertEquals(Integer.valueOf(3), arr.get(0));
        assertEquals(3, arr.size());

//        arr = Solution.graduateStudents();
//        assertEquals(Integer.valueOf(2), arr.get(0));
//        assertEquals(1, arr.size());

    }
}


