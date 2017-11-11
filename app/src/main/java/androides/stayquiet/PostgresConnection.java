package androides.stayquiet;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by developer on 11/11/17.
 */

public class PostgresConnection extends AsyncTask<String, Void, String> {
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgres://wxvbouopbnqwqw:5419f6dbc5f2d0d83b526a1310f790aa15eb8e552fd1a077ce59dacc3d222bea@ec2-46-137-97-169.eu-west-1.compute.amazonaws.com:5432/d4svl2ohrbs43c";
    static final String DB_USER = "wxvbouopbnqwqw";
    static final String DB_PASS = "5419f6dbc5f2d0d83b526a1310f790aa15eb8e552fd1a077ce59dacc3d222bea";

    @Override
    protected void onPreExecute() {
        //En esta parte del método es donde normalmente
        //creamos un cuadro de carga (ProgressDialog)
    }

    @Override
    protected String doInBackground(String... urls) {
        //En esta parte es donde realizamos la llamada. Se realiza de manera asíncrona
        Connection conn = null;
        Statement st = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            st = conn.createStatement();
            String sql;
            sql = "SELECT  first, last FROM Employees";
            ResultSet rs = st.executeQuery(sql);

            //STEP 5: Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                String first = rs.getString("first");
                String last = rs.getString("last");

                //Display values
                System.out.print(", First: " + first);
                System.out.println(", Last: " + last);
            }
            //STEP 6: Clean-up environment
            rs.close();
            st.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (st != null)
                    st.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }

        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        //En esta parte es donde tratamos el resultado devuelto por la llamada a la BBDD.
    }
}
