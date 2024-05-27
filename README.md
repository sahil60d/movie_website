cs122bProject

- # General
    - #### Team#: 96
    
    - #### Names: Sahil Desai
    
    - #### Project 4 Video Demo Link: https://drive.google.com/file/d/1C49h_JYbywXRqZN1fEmYiN-Wrn7yjb5W/view?usp=sharing


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.

    Set flag for cachePrepStmts:
    cs122b-project1-api-example/WebContent/META-INF/context.xml

    Prepared Statements + JDBC Connection Pooling:
    cs122b-project1-api-example/src/AddMovieServlet.java
    cs122b-project1-api-example/src/AddStarServlet.java
    cs122b-project1-api-example/src/AutocompleteServlet.java
    cs122b-project1-api-example/src/CartServlet.java
    cs122b-project1-api-example/src/DatabaseMetadataServlet.java
    cs122b-project1-api-example/src/EmployeeDAO.java
    cs122b-project1-api-example/src/EmployeeLoginServlet.java
    cs122b-project1-api-example/src/LoginServlet.java
    cs122b-project1-api-example/src/MoviesServlet.java
    cs122b-project1-api-example/src/PaymentServlet.java
    cs122b-project1-api-example/src/SingleMovieServlet.java
    cs122b-project1-api-example/src/SingleStarServlet.java
    cs122b-project1-api-example/src/StarsServlet.java

    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    Connection pooling is used to efficiently manage database connections for the application. When a servlet needs to interact with the database, it retrieves a connection from this pool using a JNDI lookup to a DataSource object. This avoids the costly process of creating a new connection for each user request. It also closed PreparedStatment and Connection and returns the connection to the pool for reuse instead of terminating the connection which significantly improves performance.
    
    - #### Explain how Connection Pooling works with two backend SQL.
    With 2 backend SQL servers, master and slave, connection pooling is set up for each one. When a servlet needs to perform a read/write it retrieves the conection from the appropriate pool, master for reads and write and slave for reads. This helps balance the load and improve performance. 
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    I configured the context.xml file to have an additional resource "jdbc/mastermoviedb" that I use any time that a servlet needs to perform a write.

    cs122b-project1-api-example/WebContent/META-INF/context.xml

    Since all the writing operations are used in the employee pages, to add movies and stars, I used mastermoviedb database connection there and the moviedb connection in the other servlets that only perform reads.

    - #### How read/write requests were routed to Master/Slave SQL?
    The mastermoviedb connection uses the master instance ip in its url to use the master instance for write operations. I had to configure this after depolying it to the instances or it wouldn't work locally.



Project 3:
Demo: https://drive.google.com/file/d/1fn-ENzY9ZmTEPPdWgshnQ_oIxDklB4rM/view?usp=sharing

Stored Procedure: cs122b-project1-api-example/add-movie.sql

Inconsistencies:
Missing Field Requirements: There were missing fields that I needed to account for to be consistent with the attributes having NOT NULL in the schema.
The category codes also didn't aline with the existing genres so I used a hash map to organize them.

Optimization Strategies:
I used Batch Loading and Hash Maps to improve the efficiency of parsing. Batch loading doesn't load all the memory at once which helps reduce memory usage and hash maps have faster lookups, insertions, and deletions than lists. 
