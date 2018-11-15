package com.team.jdbi.db;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface PersonDAO {
    @SqlUpdate("create table person (id int primary key, name varchar(100))")
    void createPersonTable();

    @SqlUpdate("insert into person (id, name) values (:id, :name)")
    void insert(@Bind("id") int id, @Bind("name") String name);

    @SqlQuery("select name from person where id = :id")
    String findNameById(@Bind("id") int id);

    @SqlUpdate("delete from person where id = :id")
    void deleteById(@Bind("id") int id);
}
