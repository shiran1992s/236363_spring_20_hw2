package HW2;

import HW2.business.*;
import HW2.data.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import HW2.data.PostgreSQLErrorCodes;
import javafx.util.Pair;

import java.util.ArrayList;

import static HW2.business.ReturnValue.*;



public class Solution {

    private static void createTable(String name, ArrayList<Pair<String, String>> typedSchema) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;

        try {
            StringBuilder string_builder = new StringBuilder();
            string_builder.append("CREATE TABLE " + name + "\n" + "(\n");
            for (Pair field : typedSchema)
                string_builder.append("    " + field.getKey() + " " + field.getValue() + ",\n");
            string_builder.deleteCharAt(string_builder.length()-2);             //delete last ","
            string_builder.append(")");
            pstmt = connection.prepareStatement(string_builder.toString());

            pstmt.execute();

        } catch (SQLException e) {
//            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
//                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
//                e.printStackTrace();
            }
        }
    }


    public static void createTables() {
        InitialState.createInitialState();
        //create your tables here
        ArrayList<Pair<String, String>> typedSchema = new ArrayList<>();
        typedSchema.add(new Pair("course_id","integer NOT NULL"));
        typedSchema.add(new Pair("semester","integer NOT NULL"));
        typedSchema.add(new Pair("time","integer NOT NULL"));
        typedSchema.add(new Pair("room","integer NOT NULL"));
        typedSchema.add(new Pair("day","integer NOT NULL"));
        typedSchema.add(new Pair("credit_points","integer NOT NULL"));
        typedSchema.add(new Pair("PRIMARY KEY","(course_id)"));
        typedSchema.add(new Pair("UNIQUE","(course_id)"));
        typedSchema.add(new Pair("UNIQUE","(semester)"));
        typedSchema.add(new Pair("CHECK","(course_id > 0)"));
        typedSchema.add(new Pair("CHECK","(room > 0)"));
        typedSchema.add(new Pair("CHECK","(credit_points > 0)"));
        createTable("Test", typedSchema);
        typedSchema.clear();

        typedSchema.add(new Pair("supervisor_id","integer NOT NULL"));
        typedSchema.add(new Pair("supervisor_name","char[] NOT NULL"));
        typedSchema.add(new Pair("salary","integer NOT NULL"));
        typedSchema.add(new Pair("PRIMARY KEY","(supervisor_id)"));
        typedSchema.add(new Pair("UNIQUE","(supervisor_id)"));
        typedSchema.add(new Pair("CHECK","(supervisor_id > 0)"));
        typedSchema.add(new Pair("CHECK","(salary >= 0)"));
        createTable("Supervisor", typedSchema);
        typedSchema.clear();

        typedSchema.add(new Pair("student_id","integer NOT NULL"));
        typedSchema.add(new Pair("student_name","char[] NOT NULL"));
        typedSchema.add(new Pair("faculty","char[] NOT NULL"));
        typedSchema.add(new Pair("credit_points","integer NOT NULL"));
        typedSchema.add(new Pair("PRIMARY KEY","(student_id)"));
        typedSchema.add(new Pair("UNIQUE","(student_id)"));
        typedSchema.add(new Pair("CHECK","(student_id > 0)"));
        typedSchema.add(new Pair("CHECK","(credit_points >= 0)"));
        createTable("Student", typedSchema);
        typedSchema.clear();
    }

    public static void clearTables() {
        //clear your tables here
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT table_name\n" +
                    "  FROM information_schema.tables\n" +
                    "  WHERE table_schema='public'\n" +
                    "   AND table_type='BASE TABLE';");
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next())
            {
                String tableName = resultSet.getString("table_name");
                pstmt = connection.prepareStatement("DELETE FROM  " + tableName + " CASCADE");
                pstmt.execute();
            }
            resultSet.close();
        } catch (SQLException e) {
            //e.printStackTrace()();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }
    }

    public static void dropTables() {
        InitialState.dropInitialState();
		//drop your tables here
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            // drop views;
            pstmt = connection.prepareStatement("SELECT table_name\n" +
                    "  FROM information_schema.tables\n" +
                    "  WHERE table_schema='public'\n" +
                    "   AND table_type='VIEW';");
            ResultSet resultSet1 = pstmt.executeQuery();

            while (resultSet1.next())
            {
                String viewName = resultSet1.getString("table_name");
                pstmt = connection.prepareStatement("DROP VIEW IF EXISTS " + viewName + " CASCADE");
                pstmt.execute();
            }
            resultSet1.close();

            //drop tables;
            pstmt = connection.prepareStatement("SELECT table_name\n" +
                    "  FROM information_schema.tables\n" +
                    "  WHERE table_schema='public'\n" +
                    "   AND table_type='BASE TABLE';");
            ResultSet resultSet2 = pstmt.executeQuery();

            while (resultSet2.next())
            {
                String tableName = resultSet2.getString("table_name");
                pstmt = connection.prepareStatement("DROP TABLE IF EXISTS " + tableName + " CASCADE");
                pstmt.execute();
            }
            resultSet2.close();
        } catch (SQLException e) {
            //e.printStackTrace()();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
        }
    }

    public static ReturnValue addTest(Test test) {
       return OK;
    }

    public static Test getTestProfile(Integer testID, Integer semester) {
        return new Test();
    }

    public static ReturnValue deleteTest(Integer testID, Integer semester) {
		return OK;
    }

    public static ReturnValue addStudent(Student student) {
        return OK;
    }

    public static Student getStudentProfile(Integer studentID) {
        return new Student();
    }

    public static ReturnValue deleteStudent(Integer studentID) {
        return OK;
    }

    public static ReturnValue addSupervisor(Supervisor supervisor) {
        return OK;
    }

    public static Supervisor getSupervisorProfile(Integer supervisorID) {
        return new Supervisor();
    }

    public static ReturnValue deleteSupervisor(Integer supervisorID) {
        return OK;
    }

    public static ReturnValue studentAttendTest(Integer studentID, Integer testID, Integer semester) {
        return OK;
    }

    public static ReturnValue studentWaiveTest(Integer studentID, Integer testID, Integer semester) {
        return OK;
    }

    public static ReturnValue supervisorOverseeTest(Integer supervisorID, Integer testID, Integer semester) {
       return OK;
    }

    public static ReturnValue supervisorStopsOverseeTest(Integer supervisorID, Integer testID, Integer semester) {
       return OK;
    }

    public static Float averageTestCost() {
        return 0.0f;
    }

    public static Integer getWage(Integer supervisorID) {
        return 0;
    }

    public static ArrayList<Integer> supervisorOverseeStudent() {
        return new ArrayList<Integer>();
    }

    public static ArrayList<Integer> testsThisSemester(Integer semester) {
        return new ArrayList<Integer>();
    }

    public static Boolean studentHalfWayThere(Integer studentID) {
        return true;
    }

    public static Integer studentCreditPoints(Integer studentID) {
        return 0;
    }

    public static Integer getMostPopularTest(String faculty) {
        return 0;
    }

    public static ArrayList<Integer> getConflictingTests() {
        return new ArrayList<Integer>();
    }

    public static ArrayList<Integer> graduateStudents() {
        return new ArrayList<Integer>();
    }

    public static ArrayList<Integer> getCloseStudents(Integer studentID) {
        return new ArrayList<Integer>();
    }
}

