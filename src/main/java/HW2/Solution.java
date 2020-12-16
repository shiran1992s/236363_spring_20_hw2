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

    private static void createTable(String name, ArrayList<Pair<String, String>> schema) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;

        try {
            StringBuilder string_builder = new StringBuilder();
            string_builder.append("CREATE TABLE " + name + "\n" + "(\n");
            for (Pair field : schema)
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
        try {
            InitialState.createInitialState();
            //create your tables here
            ArrayList<Pair<String, String>> schema = new ArrayList<>();
            schema.add(new Pair("course_id", "integer NOT NULL"));
            schema.add(new Pair("semester", "integer NOT NULL"));
            schema.add(new Pair("time", "integer NOT NULL"));
            schema.add(new Pair("room", "integer NOT NULL"));
            schema.add(new Pair("day", "integer NOT NULL"));
            schema.add(new Pair("credit_points", "integer NOT NULL"));
            schema.add(new Pair("PRIMARY KEY", "(course_id, semester)"));
            schema.add(new Pair("UNIQUE", "(course_id ,semester)"));
            schema.add(new Pair("CHECK", "(course_id > 0)"));
            schema.add(new Pair("CHECK", "(room > 0)"));
            schema.add(new Pair("CHECK", "(credit_points > 0)"));
            createTable("Test", schema);
            schema.clear();

            schema.add(new Pair("supervisor_id", "integer NOT NULL"));
            schema.add(new Pair("supervisor_name", "text NOT NULL"));
            schema.add(new Pair("salary", "integer NOT NULL"));
            schema.add(new Pair("PRIMARY KEY", "(supervisor_id)"));
            schema.add(new Pair("UNIQUE", "(supervisor_id)"));
            schema.add(new Pair("CHECK", "(supervisor_id > 0)"));
            schema.add(new Pair("CHECK", "(salary >= 0)"));
            createTable("Supervisor", schema);
            schema.clear();

            schema.add(new Pair("student_id", "integer NOT NULL"));
            schema.add(new Pair("student_name", "text NOT NULL"));
            schema.add(new Pair("faculty", "text NOT NULL"));
            schema.add(new Pair("credit_points", "integer NOT NULL"));
            schema.add(new Pair("PRIMARY KEY", "(student_id)"));
            schema.add(new Pair("UNIQUE", "(student_id)"));
            schema.add(new Pair("CHECK", "(student_id > 0)"));
            schema.add(new Pair("CHECK", "(credit_points >= 0)"));
            createTable("Student", schema);
            schema.clear();


            schema.add(new Pair("student_id","integer NOT NULL"));
            schema.add(new Pair("course_id","integer NOT NULL"));
            schema.add(new Pair("semester","integer NOT NULL"));
            schema.add(new Pair("PRIMARY KEY","(student_id, course_id, semester)"));
            schema.add(new Pair("FOREIGN KEY","(student_id) REFERENCES Student" + "(student_id) ON DELETE CASCADE ON UPDATE CASCADE"));
            schema.add(new Pair("FOREIGN KEY","(course_id, semester) REFERENCES Test" + "(course_id, semester) ON DELETE CASCADE ON UPDATE CASCADE"));
            schema.add(new Pair("CHECK", "(course_id > 0)"));
            schema.add(new Pair("CHECK", "(student_id > 0)"));
            schema.add(new Pair("CHECK", "(semester > 0)"));
            createTable("Tested", schema);
            schema.clear();

            schema.add(new Pair("supervisor_id", "integer NOT NULL"));
            schema.add(new Pair("course_id", "integer NOT NULL"));
            schema.add(new Pair("semester", "integer NOT NULL"));
            schema.add(new Pair("PRIMARY KEY", "(supervisor_id, course_id, semester)"));
            schema.add(new Pair("FOREIGN KEY", "(supervisor_id) REFERENCES " + "Supervisor" + "(supervisor_id) ON DELETE CASCADE ON UPDATE CASCADE"));
            schema.add(new Pair("FOREIGN KEY", "(course_id, semester) REFERENCES " + "Test" + "(course_id, semester) ON DELETE CASCADE ON UPDATE CASCADE"));
            schema.add(new Pair("CHECK", "(semester > 0)"));
            schema.add(new Pair("CHECK", "(course_id > 0)"));
            schema.add(new Pair("CHECK", "(supervisor_id > 0)"));
            createTable("Supervised", schema);
            schema.clear();

//            schema.add(new Pair("Faculty", "text NOT NULL"));
//            schema.add(new Pair("Points", "integer NOT NULL"));
//            schema.add(new Pair("PRIMARY KEY", "(Faculty)"));
//            schema.add(new Pair("UNIQUE", "(Faculty)"));
//            schema.add(new Pair("CHECK", "(Points >= 0)"));
//            createTable("Credits", schema);
//            schema.clear();
//            addCredits();

            createStudentTestedSupervisedView();
            createStudentTestedCreditPointsView();

        }catch (Exception e) {
            System.out.print("dgdg");
        }
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

//    private static void addCredits() {
//        PreparedStatement pstmt = null;
//        Connection connection = DBConnector.getConnection();
//        try {
//        pstmt = connection.prepareStatement("INSERT INTO CreditPoints(Faculty,Points) VALUES (?, ?), (?, ?), (?, ?);");
//        pstmt.setString(1, "CS");
//        pstmt.setInt(2, 120);
//        pstmt.setString(3, "EE");
//        pstmt.setInt(4, 160);
//        pstmt.setString(5, "MATH");
//        pstmt.setInt(6, 115);
//        pstmt.execute();
//        } catch (SQLException e) {
//            //e.printStackTrace();
//            return;
//        }
//        finally {
//            try {
//                pstmt.close();
//            } catch (SQLException e) {
//                //e.printStackTrace()();
////                return ERROR;
//            }
//            try {
//                connection.close();
//            } catch (SQLException e) {
//                //e.printStackTrace()();
////                return ERROR;
//            }
//        }
//    }

    public static ReturnValue addTest(Test test) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("INSERT INTO Test" +
                    " VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1,test.getId());
            pstmt.setInt(2, test.getSemester());
            pstmt.setInt(3, test.getTime());
            pstmt.setInt(4, test.getRoom());
            pstmt.setInt(5, test.getDay());
            pstmt.setInt(6, test.getCreditPoints());
            pstmt.execute();
            return OK;
        } catch (SQLException e) {
            //e.printStackTrace();
            Integer errorCode = Integer.valueOf(e.getSQLState());
            if(errorCode == PostgreSQLErrorCodes.CHECK_VIOLATION.getValue()
                    || errorCode == PostgreSQLErrorCodes.NOT_NULL_VIOLATION.getValue()
                    || errorCode == PostgreSQLErrorCodes.FOREIGN_KEY_VIOLATION.getValue())
                return BAD_PARAMS;
            if(errorCode == PostgreSQLErrorCodes.UNIQUE_VIOLATION.getValue())
                return ALREADY_EXISTS;
            return ERROR;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
        }
    }

    public static Test getTestProfile(Integer testID, Integer semester) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT * FROM Test WHERE course_id = ? AND semester = ? ");
            pstmt.setInt(1,testID);
            pstmt.setInt(2,semester);

            ResultSet results = pstmt.executeQuery();
            if(results.next()) {
                Test t = new Test();
                t.setId(results.getInt(1));
                t.setSemester(results.getInt(2));
                t.setTime(results.getInt(3));
                t.setRoom(results.getInt(4));
                t.setDay(results.getInt(5));
                t.setCreditPoints(results.getInt(6));
                results.close();
                return t;
            }

            results.close();
            return Test.badTest();

        } catch (SQLException e) {
            //e.printStackTrace();
            return Test.badTest();
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

    public static ReturnValue deleteTest(Integer testID, Integer semester) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(
                    "DELETE FROM Test" +
                            " WHERE course_id = ? AND semester = ?");
            pstmt.setInt(1,testID);
            pstmt.setInt(2,semester);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 1)
                return OK;
            else if(affectedRows == 0)
                return NOT_EXISTS;
            return ERROR;
        } catch (SQLException e) {
            //e.printStackTrace()();
            return ERROR;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
        }
    }

    public static ReturnValue addStudent(Student student) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("INSERT INTO Student" +
                    " VALUES (?, ?, ?, ?)");
            pstmt.setInt(1,student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getFaculty());
            pstmt.setInt(4, student.getCreditPoints());
            pstmt.execute();
            return OK;
        } catch (SQLException e) {
            //e.printStackTrace();
            Integer errorCode = Integer.valueOf(e.getSQLState());
            if(errorCode == PostgreSQLErrorCodes.CHECK_VIOLATION.getValue()
                    || errorCode == PostgreSQLErrorCodes.NOT_NULL_VIOLATION.getValue()
                    || errorCode == PostgreSQLErrorCodes.FOREIGN_KEY_VIOLATION.getValue())
                return BAD_PARAMS;
            if(errorCode == PostgreSQLErrorCodes.UNIQUE_VIOLATION.getValue())
                return ALREADY_EXISTS;
            return ERROR;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
        }
    }

    public static Student getStudentProfile(Integer studentID) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT * FROM Student" +
                    " WHERE student_Id = ?");
            pstmt.setInt(1,studentID);

            ResultSet results = pstmt.executeQuery();
            if(results.next()) {
                Student stu = new Student();
                stu.setId(results.getInt(1));
                stu.setName(results.getString(2));
                stu.setFaculty(results.getString(3));
                stu.setCreditPoints(results.getInt(4));
                results.close();
                return stu;
            }

            results.close();
            return Student.badStudent();

        } catch (SQLException e) {
            //e.printStackTrace();
            return Student.badStudent();
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

    public static ReturnValue deleteStudent(Integer studentID) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(
                    "DELETE FROM Student" +
                            " WHERE student_id = ? ");
            pstmt.setInt(1,studentID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 1)
                return OK;
            else if(affectedRows == 0)
                return NOT_EXISTS;
            return ERROR;
        } catch (SQLException e) {
            //e.printStackTrace()();
            return ERROR;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
        }
    }

    public static ReturnValue addSupervisor(Supervisor supervisor) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("INSERT INTO  Supervisor" +
                    " VALUES (?, ?, ?)");
            pstmt.setInt(1,supervisor.getId());
            pstmt.setString(2, supervisor.getName());
            pstmt.setInt(3, supervisor.getSalary());
            pstmt.execute();
            return OK;
        } catch (SQLException e) {
            //e.printStackTrace();
            Integer errorCode = Integer.valueOf(e.getSQLState());
            if(errorCode == PostgreSQLErrorCodes.CHECK_VIOLATION.getValue()
                    || errorCode == PostgreSQLErrorCodes.NOT_NULL_VIOLATION.getValue()
                    || errorCode == PostgreSQLErrorCodes.FOREIGN_KEY_VIOLATION.getValue())
                return BAD_PARAMS;
            if(errorCode == PostgreSQLErrorCodes.UNIQUE_VIOLATION.getValue())
                return ALREADY_EXISTS;
            return ERROR;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
        }
    }

    public static Supervisor getSupervisorProfile(Integer supervisorID) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT * FROM  Supervisor" +
                    " WHERE supervisor_Id = ?");
            pstmt.setInt(1,supervisorID);

            ResultSet results = pstmt.executeQuery();
            if(results.next()) {
                Supervisor sup = new Supervisor();
                sup.setId(results.getInt(1));
                sup.setName(results.getString(2));
                sup.setSalary(results.getInt(3));
                results.close();
                return sup;
            }

            results.close();
            return Supervisor.badSupervisor();

        } catch (SQLException e) {
            //e.printStackTrace();
            return Supervisor.badSupervisor();
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

    public static ReturnValue deleteSupervisor(Integer supervisorID) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(
                    "DELETE FROM Supervisor" +
                            " WHERE supervisor_id = ? ");
            pstmt.setInt(1,supervisorID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 1)
                return OK;
            else if(affectedRows == 0)
                return NOT_EXISTS;
            return ERROR;
        } catch (SQLException e) {
            //e.printStackTrace()();
            return ERROR;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
        }
    }

    public static ReturnValue studentAttendTest(Integer studentID, Integer testID, Integer semester) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("INSERT INTO Tested" +
                    " VALUES (?, ?, ?)");
            pstmt.setInt(1,studentID);
            pstmt.setInt(2, testID);
            pstmt.setInt(3, semester);
            pstmt.execute();
            return OK;
        } catch (SQLException e) {
            //e.printStackTrace();
            Integer errorCode = Integer.valueOf(e.getSQLState());
            if(errorCode == PostgreSQLErrorCodes.CHECK_VIOLATION.getValue()
                    || errorCode == PostgreSQLErrorCodes.NOT_NULL_VIOLATION.getValue())
                return BAD_PARAMS;
            if(errorCode == PostgreSQLErrorCodes.FOREIGN_KEY_VIOLATION.getValue())
                return NOT_EXISTS;
            if(errorCode == PostgreSQLErrorCodes.UNIQUE_VIOLATION.getValue())
                return ALREADY_EXISTS;
            return ERROR;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
        }
    }

    public static ReturnValue studentWaiveTest(Integer studentID, Integer testID, Integer semester) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(
                    "DELETE FROM Tested" +
                            " WHERE student_id = ? AND course_id = ? AND semester = ?");
            pstmt.setInt(1,studentID);
            pstmt.setInt(2,testID);
            pstmt.setInt(2,semester);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 1)
                return OK;
            else if(affectedRows == 0)
                return NOT_EXISTS;
            return ERROR;
        } catch (SQLException e) {
            //e.printStackTrace();
            return ERROR;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
        }
    }

    public static ReturnValue supervisorOverseeTest(Integer supervisorID, Integer testID, Integer semester) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("INSERT INTO Supervised" +
                    " VALUES (?, ?, ?)");
            pstmt.setInt(1,supervisorID);
            pstmt.setInt(2, testID);
            pstmt.setInt(3, semester);
            pstmt.execute();
            return OK;
        } catch (SQLException e) {
            //e.printStackTrace();
            Integer errorCode = Integer.valueOf(e.getSQLState());
            if(errorCode == PostgreSQLErrorCodes.CHECK_VIOLATION.getValue()
                    || errorCode == PostgreSQLErrorCodes.NOT_NULL_VIOLATION.getValue())
                return BAD_PARAMS;
            if(errorCode == PostgreSQLErrorCodes.FOREIGN_KEY_VIOLATION.getValue())
                return NOT_EXISTS;
            if(errorCode == PostgreSQLErrorCodes.UNIQUE_VIOLATION.getValue())
                return ALREADY_EXISTS;
            return ERROR;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
        }
    }

    public static ReturnValue supervisorStopsOverseeTest(Integer supervisorID, Integer testID, Integer semester) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(
                    "DELETE FROM Supervised" +
                            " WHERE supervisor_id = ? AND course_id = ? AND semester = ?");
            pstmt.setInt(1,supervisorID);
            pstmt.setInt(2,testID);
            pstmt.setInt(2,semester);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 1)
                return OK;
            else if(affectedRows == 0)
                return NOT_EXISTS;
            return ERROR;
        } catch (SQLException e) {
            //e.printStackTrace();
            return ERROR;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
//                return ERROR;
            }
        }
    }
// TODO: NEED FIX THE QUERY - CALCULATION IS NOT ACCURATE
    public static Float averageTestCost() {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {

            pstmt = connection.prepareStatement("SELECT Avg(coalesce(average_tests, 0)) as average\n" +
                    "FROM test t LEFT OUTER JOIN" +
                    "   (SELECT course_id, semester, (SUM(salary)/COUNT(s2.supervisor_id)) AS average_tests\n" +
                    "    FROM Supervised s1 INNER JOIN Supervisor s2 ON s1.supervisor_id = s2.supervisor_id\n" +
                    "    GROUP BY course_id,semester) as AV"+ "\n" +
                    "ON t.course_id = AV.course_id AND t.semester = AV.semester");

            ResultSet results = pstmt.executeQuery();
            if(results.next()) {
                Float s = results.getFloat(1);
                results.close();
                return s;
            }
            results.close();
            return 0.0f;
        } catch (SQLException e) {
            //e.printStackTrace();
            return 0.0f;
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

    public static Integer getWage(Integer supervisorID) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(" SELECT SUM(salary) FROM Supervised INNER JOIN Supervisor" +
                    " WHERE supervisor_id = ?");
            pstmt.setInt(1,supervisorID);


            ResultSet results = pstmt.executeQuery();
            if(results.next()) {
                Integer s = results.getInt(1);
                results.close();
                return s;
            }
            results.close();
            return 0;
        } catch (SQLException e) {
            //e.printStackTrace();
            return 0;
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

    public static ArrayList<Integer> supervisorOverseeStudent() {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT student_id\n" +
                    "FROM\n" +
                    "   (SELECT student_id, supervisor_id, count(*) AS student_id_supervised\n" +
                    "    FROM StudentTestedSupervised\n" +
                    "    GROUP BY student_id, supervisor_id) as C\n" +
                    "WHERE student_id_supervised > 1\n" +
                    "ORDER BY student_id DESC\n");

            ResultSet results = pstmt.executeQuery();
            ArrayList<Integer> student_ids_more_than_once = new ArrayList<>();
            while(results.next())
                student_ids_more_than_once.add(results.getInt(1));

            results.close();
            return student_ids_more_than_once;
        } catch (SQLException e) {
            //e.printStackTrace();
            return new ArrayList<>();
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

    public static ArrayList<Integer> testsThisSemester(Integer semester) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT DISTINCT course_id\n" +
                    "FROM Test" + "\n" +
                    "WHERE Test.semester == ?)\n" +
                    "ORDER BY course_id DESC\n" +
                    "LIMIT 5");
            pstmt.setInt(1,semester);

            ResultSet results = pstmt.executeQuery();
            ArrayList<Integer> testsThisSemester = new ArrayList<>();
            while(results.next())
                testsThisSemester.add(results.getInt(1));

            results.close();
            return testsThisSemester;
        } catch (SQLException e) {
            //e.printStackTrace();
            return new ArrayList<>();
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

    public static Boolean studentHalfWayThere(Integer studentID) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {

            pstmt = connection.prepareStatement(" SELECT student_id, faculty FROM Student" +
                    " WHERE student_id = ? AND Student.credit_points >=" +
                    "(SELECT Points/2 FROM CreditPoints WHERE Student.faculty = CreditPoints.Faculty)");
            pstmt.setInt(1,studentID);

            ResultSet results = pstmt.executeQuery();
            if(results.next()) {
                results.close();
                return true;
            }
            results.close();
            return false;
        } catch (SQLException e) {
            //e.printStackTrace();
            return false;
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

    public static Integer studentCreditPoints(Integer studentID) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(" SELECT SUM(StudentTestedCreditPoints.credit_points)" +
                    " + (SELECT S.credit_points \n" +
                    "        FROM Student AS S\n" +
                    "        WHERE S.student_id = ?\n" +
                    "       ) AS student_points" +
                    " FROM StudentTestedCreditPoints" +
                    " WHERE StudentTestedCreditPoints.student_id = ?");
            pstmt.setInt(1,studentID);
            pstmt.setInt(2,studentID);

            ResultSet results = pstmt.executeQuery();
            if(results.next()) {
                Integer s = results.getInt(1);
                results.close();
                return s;
            }
            results.close();
            return 0;
        } catch (SQLException e) {
            //e.printStackTrace();
            return 0;
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

    public static Integer getMostPopularTest(String faculty) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT course_id\n" +
                    "FROM\n" +
                        "(SELECT DISTINCT student_id, course_id, COUNT(*) AS student_id_count\n" +
                        "FROM StudentTestedCreditPoints\n" +
                        "WHERE faculty = ?\n" +
                        "GROUP BY student_id, course_id) AS C\n" +
                    "ORDER BY student_id_count DESC\n" +
                    "LIMIT 1");
            pstmt.setString(1,faculty);
            ResultSet results = pstmt.executeQuery();
            if(results.next()) {
                Integer mostPopularTest = results.getInt(1);
                results.close();
                return mostPopularTest;
            }
            results.close();
            return 0;
        } catch (SQLException e) {
            //e.printStackTrace();
            return null;
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

    public static ArrayList<Integer> getConflictingTests() {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT DISTINCT CJ.course_id1\n" +
                    "FROM (SELECT t1.course_id as course_id1, t1.semester, t1.day, t1.time, t2.course_id as course_id2, t2.semester, t2.day, t2.time\n" +
                    "      FROM Test AS t1 CROSS JOIN Test AS t2\n" +
                    "      WHERE t1.course_id <> t2.course_id AND t1.semester = t2.semester\n" +
                    "      AND t1.day = t2.day AND t1.time = t2.time ) AS CJ\n" +
                    "ORDER BY CJ.course_id1 DESC\n");

            ResultSet results = pstmt.executeQuery();
            ArrayList<Integer> conflictingTests = new ArrayList<>();
            while(results.next())
                conflictingTests.add(results.getInt(1));

            results.close();
            return conflictingTests;
        } catch (SQLException e) {
            //e.printStackTrace();
            return new ArrayList<>();
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

    // NOT GOOD NEED REFACTORING
    public static ArrayList<Integer> graduateStudents() {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT DISTINCT student_id\n" +
                    "FROM " +
                    "(SELECT student_ids_and_points.student_id, student_ids_and_points.faculty, SUM(student_ids_and_points.credit_points)\n" +
                    "                    + (SELECT S.credit_points \n" +
                    "                            FROM Student AS S\n" +
                    "                          ) AS student_points" +
                    "                     FROM StudentTestedCreditPoints AS student_ids_and_points) AS student_ids \n" +
                    "WHERE student_ids_and_points.faculty = CreditPoints.Faculty AND student_points >= CreditPoints.Points\n" +
                    "ORDER BY student_id DESC\n" +
                    "LIMIT 5");

            ResultSet results = pstmt.executeQuery();
            ArrayList<Integer> graduateStudents = new ArrayList<>();
            while(results.next())
                graduateStudents.add(results.getInt(1));

            results.close();
            return graduateStudents;
        } catch (SQLException e) {
            //e.printStackTrace();
            return new ArrayList<>();
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

    public static ArrayList<Integer> getCloseStudents(Integer studentID) {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try{
        pstmt = connection.prepareStatement("SELECT student_id\n" +
                "FROM\n" +
                "   (SELECT student_id, Count(student_id) AS shared_tests\n" +
                "    FROM Tested\n" +
                "    WHERE course_id IN\n" +
                "       (SELECT course_id\n" +
                "        FROM Tested\n" +
                "        WHERE student_id = ?) AND student_id <> ?\n" +
                "    GROUP BY student_id\n" +
                "    ORDER BY shared_tests DESC, student_id\n" +
                "    ) AS ST\n" +
                "WHERE shared_tests >= (SELECT Count(student_id)\n" +
                "            FROM Tested\n" +
                "            WHERE student_id = ?)/2.0\n" +
                "ORDER BY student_id\n" +
                "LIMIT 10");
        pstmt.setInt(1,studentID);
        pstmt.setInt(2,studentID);
        pstmt.setInt(3,studentID);

        ResultSet results = pstmt.executeQuery();
        ArrayList<Integer> closeStudents = new ArrayList<>();
        while(results.next())
            closeStudents.add(results.getInt(1));
        results.close();
        return closeStudents;

    } catch (SQLException e) {
        //e.printStackTrace();
        return new ArrayList<>();
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

    private static void createStudentTestedSupervisedView() {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE OR REPLACE VIEW StudentTestedSupervised AS\n" +
                    "(\n" +
                    "    SELECT Tested.student_id, Supervised.supervisor_id," +
                    "    Supervised.course_Id, Supervised.semester\n" +
                    "    FROM Tested, Supervised\n" +
                    "    WHERE Tested.course_Id = Supervised.course_Id\n" +
                    "    AND Tested.semester = Supervised.semester\n" +
                    ")");
            pstmt = connection.prepareStatement(sb.toString());
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

    private static void createStudentTestedCreditPointsView() {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE OR REPLACE VIEW StudentTestedCreditPoints AS\n" +
                    "(\n" +
                    "    SELECT Tested.student_id, Tested.course_id," +
                    "    Tested.semester, Test.credit_points, Student.faculty \n" +
                    "    FROM Tested, Test, Student\n" +
                    "    WHERE Tested.course_Id = Test.course_Id\n" +
                    "    AND Tested.semester = Test.semester\n" +
                    "    AND Student.student_id = Tested.student_id\n" +
                    ")");
            pstmt = connection.prepareStatement(sb.toString());
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
}


