package com.kangyonggan.tradingEngine;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author kyg
 */
public class CodeGenerator {

    /**
     * 表
     */
    private static final List<String> TABLES = Arrays.asList("user_secret");

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        String projectPath = new File("").getAbsolutePath();
        String basePackage = CodeGenerator.class.getPackage().getName();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String output = projectPath + "/src/main/java";
        gc.setOutputDir(output);
        gc.setAuthor("mbg");
        gc.setOpen(false);
        gc.setFileOverride(true);
        gc.setBaseResultMap(true);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        LinkedHashMap<String, Object> jdbcProperties = loadJdbcProperties();
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(String.valueOf(jdbcProperties.get("url")));
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername(String.valueOf(jdbcProperties.get("username")));
        dsc.setPassword(String.valueOf(jdbcProperties.get("password")));
        mpg.setDataSource(dsc);

        // 自定义xml输出位置
        PackageConfig pc = new PackageConfig();
        pc.setParent(basePackage);
        String parent = "/src/main/java/" + pc.getParent().replaceAll("\\.", "/");
        Map<String, String> pathInfo = new HashMap<>(8);
        pathInfo.put(ConstVal.ENTITY_PATH, projectPath + parent + "/entity");
        pathInfo.put(ConstVal.MAPPER_PATH, projectPath + parent + "/mapper");
        pathInfo.put(ConstVal.XML_PATH, projectPath + "/src/main/resources/mapper");
        pathInfo.put(ConstVal.SERVICE_PATH, projectPath + parent + "/service");
        pathInfo.put(ConstVal.SERVICE_IMPL_PATH, projectPath + parent + "/service/impl");
        pc.setPathInfo(pathInfo);
        mpg.setPackageInfo(pc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setInclude(TABLES.toArray(new String[0]));

        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

    /**
     * 从resources/application-dev.yml中加载jdbc配置
     *
     * @return
     */
    private static LinkedHashMap<String, Object> loadJdbcProperties() {
        Yaml yaml = new Yaml();
        try {
            LinkedHashMap<String, Object> map = yaml.loadAs(new FileInputStream(new File("").getAbsolutePath() + "/src/main/resources/application-dev.yml"), LinkedHashMap.class);
            return (LinkedHashMap<String, Object>) ((LinkedHashMap<String, Object>) map.get("spring")).get("datasource");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
