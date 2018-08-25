package org.solrcn.search.compiler;

import org.solrcn.lucene.queries.formula.CustomFlumla;
import com.google.common.collect.Sets;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.solr.core.SolrCore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 动态生成Query类
 *
 * @author chenyi
 */
public class DynamicQueryClassBuilder {

    private final static Pattern SCORE_FIELD_REGX = Pattern.compile("\\w+");
    private final static String FIELD_VARIABLE_CODE_TEMPLATE = "final Floats #field#_fc = getFloatField(context.reader(),\"#field#\");";
    private final static String FIDLE_ASSIGNIN_CODE_TEMPLATE = "float #field# = #field#_fc.get(doc);";
    private final static DynamicEngine de = DynamicEngine.getInstance();

    private final String CLASS_CODE_TEMPLATE;


    public DynamicQueryClassBuilder(String luceneQueryClassTemplate) {
        this.CLASS_CODE_TEMPLATE = luceneQueryClassTemplate;
    }

    /**
     * @param flumlaStr
     * @return
     */
    private Set<String> getFieldSet(final String flumlaStr) {
        Set<String> fields = Sets.newTreeSet(new Comparator<String>() {
            // 按照字符串长度由长到短排序
            @Override
            public int compare(final String s1, final String s2) {
                if (s1.length() < s2.length()) {
                    return 1;
                }

                if (s1.length() == s2.length()) {
                    return s1.compareTo(s2);
                }
                return -1;
            }
        });

        Matcher matcher = SCORE_FIELD_REGX.matcher(flumlaStr);

        while (matcher.find()) {
            String field = matcher.group();
            if (NumberUtils.isNumber(field)) {
                continue;
            }
            fields.add(field);
        }

        return fields;
    }

    /**
     * @author: chenyi
     * @create: 2015-3-18
     * @description: 生成变量
     */
    private static String getFieldVariableCode(String field) {
        if ("tscore".equals(field)) {
            return "catalogids = hasKeyword ? FieldCache.DEFAULT.getTermsIndex(context.reader(), \"catalogid\") : SortedDocValues.EMPTY;";
        } else if ("score".equals(field) || "rawscore".equals(field) || "int".equals(field) || "long".equals(field) || "short".equals(field)) {
            return "";
        } else {
            return FIELD_VARIABLE_CODE_TEMPLATE.replace("#field#", field);
        }
    }

    /**
     * @author: chenyi
     * @create: 2015-3-18
     * @description: 变量赋值
     */
    private static String getFieldAssigninCode(String field) {
        if ("tscore".equals(field)) {
            return "float tscore = getTsScore(doc);";
        } else if ("int".equals(field) || "long".equals(field) || "short".equals(field)) {
            return "";
        } else if ("score".equals(field)) {
            return "";
        } else if ("rawscore".equals(field)) {
            return "";
        } else if ("tnslevel".equals(field)) {
            return "int tnslevel = (int)tnslevel_fc.get(doc);tnslevel = tnslevel == dh_tns ? 2000 : (10 - tnslevel) * 100;";
        } else {
            return FIDLE_ASSIGNIN_CODE_TEMPLATE.replace("#field#", field);
        }
    }

    public Class build(final CustomFlumla customFlumla) throws Exception {
        return build(customFlumla.getId(), customFlumla.getFlumlaStr());
    }

    private String getMemberVariableCode(Set<String> fields) {
        StringBuilder sb = new StringBuilder();
        for (String field : fields) {
            sb.append("\t\t\t").append(getFieldVariableCode(field)).append("\n");
        }
        return sb.toString();
    }

    private String getLocalVariableCode(Set<String> fields) {
        StringBuilder sb = new StringBuilder();
        for (String field : fields) {
            sb.append("\t\t\t").append(getFieldAssigninCode(field)).append("\n");
        }
        return sb.toString();
    }

    /**
     * @author: chenyi
     * @create: 2015-3-18
     * @description: 生成debug信息
     */
    private String getExplainDescCode(Set<String> fields) {
        StringBuilder sb = new StringBuilder();
        for (String field : fields) {
            if ("int".equals(field) || "long".equals(field) || "short".equals(field)) {
                continue;
            }
            sb.append("\t\t\t")
                    .append("explainDesc = explainDesc.replaceAll(\"" + field + "\", " + field + "+\"\");")
                    .append("\n");
        }
        return sb.toString();
    }

    /**
     * @param flumlaName 公式名
     * @param flumlaStr  公式数学表达式
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public Class build(final String flumlaName, final String flumlaStr) throws Exception {
        Set<String> fields = getFieldSet(flumlaStr);

        String className = "FlumlaQuery" + flumlaName;
        String source = CLASS_CODE_TEMPLATE;
        String memberVariable = getMemberVariableCode(fields);
        String localVariable = getLocalVariableCode(fields);
        String explainDesc = getExplainDescCode(fields);

        source = source.replaceAll("\\{classname\\}", className);
        source = source.replaceAll("\\{formula\\}", flumlaStr);
        source = source.replaceAll("\\{member_variable\\}", memberVariable);
        source = source.replaceAll("\\{local_variable\\}", localVariable);
        source = source.replaceAll("\\{explain\\}", explainDesc);
		dumpSource(className, source);
        return de.javaCodeToObject(className, source);
    }

    private void dumpSource(String className, String source) {
        String srcdump = "srcdump";
        if (!Files.exists(Paths.get(srcdump))) {
            try {
                Files.createDirectory(Paths.get(srcdump));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String classFileName = srcdump + "/" + className + ".java";
        Path p = Paths.get(classFileName);
        try {
            Files.deleteIfExists(p);
            Files.write(p, source.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            SolrCore.log.warn("dump javaclass file error {}", classFileName);
        }
    }

    public boolean testClassTemplate() {
        try {
            return (build(new CustomFlumla("lucene", "score")).getConstructors()[1] != null);
        } catch (Exception e) {
            SolrCore.log.warn("ClassTemplate Compiler test faild", e);
            return false;
        }
    }
}
