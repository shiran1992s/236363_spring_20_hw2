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

        typedSchema.add(new Pair("student_id","integer NOT NULL"));
        typedSchema.add(new Pair("course_id","integer NOT NULL"));
        typedSchema.add(new Pair("semester","integer NOT NULL"));
        typedSchema.add(new Pair("PRIMARY KEY","(course_id, student_id)"));
        typedSchema.add(new Pair("FOREIGN KEY","(student_id) REFERENCES " + "Student" + "(student_id) ON DELETE CASCADE ON UPDATE CASCADE"));
        typedSchema.add(new Pair("FOREIGN KEY","(course_id) REFERENCES " + "Test" +"(course_id) ON DELETE CASCADE ON UPDATE CASCADE"));
        typedSchema.add(new Pair("CHECK","(course_id > 0)"));
        typedSchema.add(new Pair("CHECK","(student_id > 0)"));
        createTable("Tested", typedSchema);
        typedSchema.clear();

        typedSchema.add(new Pair("supervisor_id","integer NOT NULL"));
        typedSchema.add(new Pair("course_id","integer NOT NULL"));
        typedSchema.add(new Pair("semester","integer NOT NULL"));
        typedSchema.add(new Pair("PRIMARY KEY","(course_id, supervisor_id)"));
        typedSchema.add(new Pair("FOREIGN KEY","(supervisor_id) REFERENCES " + "Supervisor" + "(supervisor_id) ON DELETE CASCADE ON UPDATE CASCADE"));
        typedSchema.add(new Pair("FOREIGN KEY","(course_id) REFERENCES " + "Test" +"(course_id) ON DELETE CASCADE ON UPDATE CASCADE"));
        typedSchema.add(new Pair("CHECK","(semester > 0)"));
        typedSchema.add(new Pair("CHECK","(course_id > 0)"));
        typedSchema.add(new Pair("CHECK","(supervisor_id > 0)"));
        createTable("Supervised", typedSchema);
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
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("INSERT INTO " + "Test" +
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
            pstmt = connection.prepareStatement("SELECT * FROM " + "Test" + " WHERE course_id = ? AND semester = ? ");
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
                    "DELETE FROM " + "Test" +
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
            pstmt = connection.prepareStatement("INSERT INTO " + "Student" +
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
            pstmt = connection.prepareStatement("SELECT * FROM " + "Student" +
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
                    "DELETE FROM " + "Student" +
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
            pstmt = connection.prepareStatement("INSERT INTO " + "Supervisor" +
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
            pstmt = connection.prepareStatement("SELECT * FROM " + "Supervisor" +
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
                    "DELETE FROM " + "Supervisor" +
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
            pstmt = connection.prepareStatement("INSERT INTO " + "Tested" +
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
                    "DELETE FROM " + "Tested" +
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
            pstmt = connection.prepareStatement("INSERT INTO " + "Supervised" +
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
                    "DELETE FROM " + "Supervised" +
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

