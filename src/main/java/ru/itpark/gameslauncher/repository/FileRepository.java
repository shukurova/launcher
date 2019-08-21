package ru.itpark.gameslauncher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
@RequiredArgsConstructor
public class FileRepository {

    private NamedParameterJdbcTemplate template;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    //TODO: дописать запрос на сохранение данных картинки в БД
    public void saveFileData() {
        template.update();
    }
}
