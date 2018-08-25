package org.solrcn.search.solr.config.test;

import org.solrcn.lucene.queries.formula.CustomFlumla;
import org.solrcn.lucene.queries.formula.CustomScoreServer;
import org.solrcn.search.compiler.DynamicQueryClassBuilder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class CustomScoreServerTest {

    public static void init(CustomScoreServer scoreServer) throws Exception {
        CustomFlumla flumla;
        flumla = new CustomFlumla("BestMatch", "score * tscore *" + " (gmvscore * (5 - imprweight)"
                + " + newproreward * imprweight+goodimage_fi" + " +  picscore + fpc + fpa)"
                + " + businesscore+dmrslevelscore_ii + cidf + allf + tnslevel");
        scoreServer.addFlumla("4", flumla);

        flumla = new CustomFlumla("BestPrice", "tscore*(gmvscore * 2.0 + pricescore * 6.0 + goodimage_fi)"
                + " + disputescore*15.0 + repeatscore*15.0 + refundscore*6.0");
        scoreServer.addFlumla("6", flumla);

        flumla = new CustomFlumla("HotProduct", "tscore * disputescore * (orderscore * 6.0 + clickscore * 3.0 + goodimage_fi) + repeatscore * 10.0 ");
        scoreServer.addFlumla("3", flumla);

        flumla = new CustomFlumla("NewProduct", "tscore * (newprosortscore + picscore * 2.0 + goodimage_fi)");
        scoreServer.addFlumla("2", flumla);

        flumla = new CustomFlumla("ProductReview", "tscore * (reviewscore + reviewtimescore * 100.0 + goodimage_fi)");
        scoreServer.addFlumla("5", flumla);
    }

    public static Constructor getDh_NS(CustomScoreServer cloudScoreServer, String id) {
        return cloudScoreServer.getFlumlaById(id);
    }

    public static void main(String[] args) throws Exception {
        boolean initzk = true;

        CustomScoreServer scoreServer = new CustomScoreServer("zk1,zk2,zk3", "www");


        while (!scoreServer.isConnected()) {
            Thread.sleep(1);
        }

        if (initzk) {
            byte[] readAllBytes = null;
            try {
                Path path = Paths.get("src/main/resources/FlumlaQueryTemplate.template");
                System.out.println(path.toAbsolutePath());
                readAllBytes = Files.readAllBytes(path);
            } catch (NoSuchFileException nfe) {
//				readAllBytes = Files.readAllBytes(Paths.get("searchmaster/solr/lib/FlumlaQueryTemplate.template"));
            }
            if (readAllBytes == null) System.exit(0);

            DynamicQueryClassBuilder dynamicQueryClassBuilder = new DynamicQueryClassBuilder(new String(readAllBytes));
            scoreServer.uploadClassTemplate(readAllBytes);
            init(scoreServer);
            System.exit(0);
        }

        String KA_SCORE = "+newkaseller_fi*0.3784+oldkaseller_fi*0.3784";
        String KASUPPORT = "+kasupport_fi*0.1";
        KA_SCORE += KASUPPORT;

        String LRSTR = "score*0.0391+tscore*0.3784+gmvscore*0.1032+reviewscore*0.0097+activescore*0.06+brandscore_fi*0.0054+categroups*0.2098+ciddmrsscore*0.0005+cidprice_fi*0.0035+clickscore*0.0146+conversionrate*0.0728+createdatescore*0.0433+dhlinkmodel_fi*0.0361+disputerate_fi*0.0019+disputescore*0.408+dmrslevelscore_ii*0.0098+experiencescore*0.1789+freeshippingscore*0.1448+historyprice_fi*0.0593+isbcpro*0.066+iscompany*0.0272+leadingtime_fi*0.0229+orderscore*0.0409+picscore*0.0418+productchannelscore*0.0682+profeedbackscore*0.0804+refundrate_fi*0.1906+refundscore*0.4174+repeatscore*0.2698+reviewtimescore*0.18+salesrate_fi*0.8648+salesvolume_fi*0.484+sellerlevelscore*0.0723+sincerity*0.0792+vasscore_fi*0.0052";
//		scoreServer.addFlumla("1A", new CustomFlumla("BestMatchB", LRSTR+"+conversiontype_fi*0.3+allf+tnslevel"+KA_SCORE));
//		scoreServer.addFlumla("1B", new CustomFlumla("BestMatchB", LRSTR+"+conversiontype_fi*0.3+allf+tnslevel+conversiontypesc_fi*0.043+conversiontypeso_fi*0.0147"+KA_SCORE));
//		scoreServer.addFlumla("1C", new CustomFlumla("BestMatchB", LRSTR+"+conversiontype_fi*0.3+allf+tnslevel+conversiontypesc_fi*0.043+conversiontypeso_fi*0.0147+prodsalescore_fi"+KA_SCORE));
//		scoreServer.addFlumla("1D", new CustomFlumla("BestMatchB", LRSTR+"+conversiontype_fi*0.3+allf+tnslevel+conversiontypesc_fi*0.043+conversiontypeso_fi*0.0147+prodsalescore_fi+goodimage_fi*0.1668"+KA_SCORE));

        scoreServer.addFlumla("1D", new CustomFlumla("BestMatchB", "score*0.3+tscore+goodimage_fi*0.5+specialpromo_fi*0.5+gmvscore*0.486+reviewscore*0.0093+activescore*0.0801+categroups*0.0107+ciddmrsscore*0.0028+cidprice_fi*0.0923+clickscore*0.0073+conversionrate*0.0785+createdatescore*0.0944+dhlinkmodel_fi*0.0055+disputerate_fi*0.0977+disputescore*0.3897+dmrslevelscore_ii*0.0092+experiencescore*0.1364+freeshippingscore*0.1277+historyprice_fi*0.0652+isbcpro*0.1042+iscompany*0.1279+leadingtime_fi*0.0964+orderscore*0.0744+picscore*0.1168+productchannelscore*0.0492+profeedbackscore*0.1648+refundrate_fi*0.1382+refundscore*0.4033+repeatscore*0.1607+reviewtimescore*0.3961+salesrate_fi*0.6755+salesvolume_fi*0.635+sellerlevelscore*0.0438+conversiontype_fi*0.0556+sincerity*0.0748+oldkaseller_fi*0.1522+newkaseller_fi*0.0006+conversiontypesc_fi*0.0477+conversiontypeso_fi*0.0265+sellerlisting_fi*0.1075+conversionclickavg_fi*0.0379+conversionorderavg_fi*0.0379+isgroupbuying_fi*0.0307+allf+tnslevel" + KA_SCORE));
        /** 应用109 发布机从hdfs下载公式 **/
        if (args != null && args.length > 0 && args[0].trim().length() > 0) {
            LRSTR = FileUtils.readFileToString(new File(args[0]));
            LRSTR = LRSTR.replaceAll("gmvscore\\*[\\d\\.]+", "gmvscore*0.1032");
            LRSTR = LRSTR.replaceAll("newkaseller_fi\\*[\\d\\.]+", "newkaseller_fi*0.3784");
            LRSTR = LRSTR.replaceAll("oldkaseller_fi\\*[\\d\\.]+", "oldkaseller_fi*0.3784");
            System.out.println("LRSTR from hdfs: ");
            System.out.println(LRSTR);
        }
        LRSTR = "score*0.1518+tscore*0.3035+specialpromo_fi*0.5+gmvscore*0.486+reviewscore*0.0093+activescore*0.0801+categroups*0.0107+ciddmrsscore*0.0028+cidprice_fi*0.0923+clickscore*0.0073+conversionrate*0.0785+createdatescore*0.0944+dhlinkmodel_fi*0.0055+disputerate_fi*0.0977+disputescore*0.3897+dmrslevelscore_ii*0.0092+experiencescore*0.1364+freeshippingscore*0.1277+historyprice_fi*0.0652+isbcpro*0.1042+iscompany*0.1279+leadingtime_fi*0.0964+orderscore*0.0744+picscore*0.1168+productchannelscore*0.0492+profeedbackscore*0.1648+refundrate_fi*0.1382+refundscore*0.4033+repeatscore*0.1607+reviewtimescore*0.3961+salesrate_fi*0.6755+salesvolume_fi*0.635+sellerlevelscore*0.0438+conversiontype_fi*0.0556+sincerity*0.0748+oldkaseller_fi*0.1522+newkaseller_fi*0.0006+goodimage_fi*0.1803+conversiontypesc_fi*0.0477+conversiontypeso_fi*0.0265+sellerlisting_fi*0.1075+conversionclickavg_fi*0.0379+conversionorderavg_fi*0.0379+isgroupbuying_fi*0.0307" + KA_SCORE;

        CustomFlumla flumla = new CustomFlumla("BestMatch", LRSTR + "+allf+tnslevel");
        /** cidf 特殊加分 **/
        CustomFlumla flumlacidf = new CustomFlumla("flumlacidf", LRSTR + "+allf+tnslevel+cidspecial_fi*15");
        scoreServer.addFlumla("019018", flumlacidf);
        scoreServer.addFlumla("102", flumlacidf);
        scoreServer.addFlumla("117", flumlacidf);
        scoreServer.addFlumla("1E", flumla);
        scoreServer.addFlumla("1", flumla);
        scoreServer.addFlumla("listvoice", flumla);

        String catalog = "gmvscore*0.414+reviewscore*0.0099+activescore*0.0287+categroups*0.0681+ciddmrsscore*0.0021+cidprice_fi*0.0868+clickscore*0.0319+conversionrate*0.087+createdatescore*0.0511+dhlinkmodel_fi*0.0104+disputerate_fi*0.0765+disputescore*0.3776+dmrslevelscore_ii*0.0084+experiencescore*0.1685+freeshippingscore*0.1476+historyprice_fi*0.0564+isbcpro*0.0988+iscompany*0.0996+leadingtime_fi*0.0885+orderscore*0.0705+picscore*0.0669+productchannelscore*0.0139+profeedbackscore*0.1662+refundrate_fi*0.1765+refundscore*0.3907+repeatscore*0.1684+reviewtimescore*0.2969+salesrate_fi*0.8382+salesvolume_fi*0.6207+sellerlevelscore*0.0349+sincerity*0.0691+conversiontype_fi*0.0059+oldkaseller_fi*0.142+newkaseller_fi*0.01+goodimage_fi*0.1617+conversiontypesc_fi*0.0393+conversiontypeso_fi*0.0134+vasscore_fi*0.1258+kasupport_fi*0.0052+sellerlisting_fi*0.116+allf+tnslevel";
        CustomFlumla catalogFluma = new CustomFlumla("catalog", catalog);
        scoreServer.addFlumla("catalog", catalogFluma);

//		//---2017-06-20
//		CustomFlumla flumla135 = new CustomFlumla("135", LRSTR+"+allf+tnslevel+prodsalescore_fi");
//		scoreServer.addFlumla("135", flumla135);
//		//---//

        CustomFlumla flumla102007 = new CustomFlumla("flumla102007", LRSTR + "+allf+tnslevel+cidspecial_fi*10+cidprice_fi*0.35+cidspecial_fi*0.5");
        scoreServer.addFlumla("102007", flumla102007);
        CustomFlumla flumlaM102007 = new CustomFlumla("flumlaM102007", LRSTR + "+allf+tnslevel+mobilepromrank_fi*0.5+cidspecial_fi*10+cidprice_fi*0.35+cidspecial_fi*0.5");
        scoreServer.addFlumla("M102007", flumlaM102007);

        CustomFlumla flumlaMdef = new CustomFlumla("Mdef", LRSTR + "+allf+tnslevel+mobilepromrank_fi*0.5+prodsalescore_fi");
        scoreServer.addFlumla("Mdef", flumlaMdef);
//		//---2017-06-20
//		CustomFlumla flumlaM135 = new CustomFlumla("M135", LRSTR+"+allf+tnslevel+mobilepromrank_fi*0.5+prodsalescore_fi");
//		scoreServer.addFlumla("M135", flumlaM135);
//		scoreServer.addFlumla("M135A", flumlaMdef);
//		//---//

        /** cidf 特殊加分 **/
        CustomFlumla flumlaMdefc = new CustomFlumla("Mdefc", LRSTR + "+allf+tnslevel+mobilepromrank_fi*0.5+cidspecial_fi*10");
        scoreServer.addFlumla("Mdefc", flumlaMdefc);


        /** LISTPPCB2B **/
        CustomFlumla flumla500 = new CustomFlumla("BestMatch", "score*0.1162+tscore*0.2323+gmvscore*0.1946+reviewscore*0.0101+activescore*0.0164+categroups*0.0795+ciddmrsscore*0.0012+cidprice_fi*0.0571+clickscore*0.0406+conversionrate*0.0877+createdatescore*0.0402+dhlinkmodel_fi*0.0143+disputerate_fi*0.0408+disputescore*0.3693+dmrslevelscore_ii*0.0088+experiencescore*0.1543+freeshippingscore*0.128+historyprice_fi*0.0621+isbcpro*0.0872+iscompany*0.0375+leadingtime_fi*0.0764+orderscore*0.051+picscore*0.0508+productchannelscore*0.0291+profeedbackscore*0.1524+refundrate_fi*0.165+refundscore*0.3818+repeatscore*0.1801+reviewtimescore*0.2747+salesrate_fi*0.7607+salesvolume_fi*0.5243+sellerlevelscore*0.0481+sincerity*0.0246+goodimage_fi*0.1813" + KA_SCORE);
        scoreServer.addFlumla("500", flumla500);

        /** 降价优先公式historyprice_fi*5 **/

        /**2015-09-24 TPS服装类目新算法,分类目计算TP ciddmrsscore **/

        /** DISCOUNT项目(促销产品靠前，亚洲调用，新SEO页面）**/
        CustomFlumla flumla36 = new CustomFlumla("BestMatch", "score * tscore *" + " (gmvscore * (5 - imprweight)"
                + " + newproreward * imprweight" + " +  picscore + fpc + fpa )"
                + " + businesscore+dmrslevelscore_ii + leadingtime_fi + cidf + allf + conversionrate * 7 + dispro*40 + goodimage_fi + tnslevel");
        scoreServer.addFlumla("36", flumla36);   //2015-10-09 测试

        CustomFlumla flumlaRecentList = new CustomFlumla("RecentList", "score+tscore+gmvscore+createdatescore*200+goodimage_fi+allf+tnslevel");
        scoreServer.addFlumla("RecentList", flumlaRecentList);

        /**
         客服推荐产品个性化排序：
         1.移动促销因子加权
         2.gmv因子和30天订单数量因子加权
         3.TP卖家加权
         4.诚保因子加权
         */
        CustomFlumla flumlaCRMS = new CustomFlumla("CRMS", "score*tscore*(gmvscore*5+mobilepromrank_fi*5+dmrslevelscore_ii+reviewscore*0.1+picscore+salesvolume_fi*5+sincerity+goodimage_fi)+businesscore+allf+conversiontype_fi*7+tnslevel");
        scoreServer.addFlumla("CRMS", flumlaCRMS);

        /**
         * 主题搜索
         */
        CustomFlumla subject1 = new CustomFlumla("subject1", "score*subjectrank");
        scoreServer.addFlumla("subject1", subject1);


        //--------------------------------------------------------------------------------------------------------------------//
        //--------------------------------------------------------------------------------------------------------------------//
        //	*************** 	下面为行业自定义公式            下面为行业自定义公式 		下面为行业自定义公式    	下面为行业自定义公式	***********************
        //--------------------------------------------------------------------------------------------------------------------//
        //--------------------------------------------------------------------------------------------------------------------//

        /**
         手机行业：
         1.	品牌（需要等品牌数据）
         2.	新上传
         3.	价格
         4.	不良体验率
         5.	重复购买率
         6.	海外备货>有备货>无备货
         7.	企业级
         如果分流 新算法后缀从B开始
         */
        CustomFlumla flumla105 = new CustomFlumla("cellphone", "score*tscore*(cidspecial_fi*10+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+brandscore_fi*3+createdatescore*3*iscompany+cidprice_fi+historyprice_fi+leadingtime_fi+picscore+categroups+repeatscore+reviewscore*0.05+activescore+experiencescore+abroadinventoryscore*2+instock+iscompany+ciddmrsscore*0.2+catalogexternalscore*3+goodimage_fi)+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("105", flumla105);
        CustomFlumla flumlaM105 = new CustomFlumla("M105", "score*tscore*(cidspecial_fi*10+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+brandscore_fi*3+createdatescore*3*iscompany+cidprice_fi+historyprice_fi+leadingtime_fi+picscore+categroups+repeatscore+reviewscore*0.05+activescore+experiencescore+abroadinventoryscore*2+instock+iscompany+ciddmrsscore*0.2+catalogexternalscore*3+mobilepromrank_fi*2.5+goodimage_fi)+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("M105", flumlaM105);

        CustomFlumla flumla105008 = new CustomFlumla("cellphone", "score*tscore*(cidspecial_fi*10+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+brandscore_fi*3+createdatescore*3*iscompany+cidprice_fi+historyprice_fi+leadingtime_fi+picscore+categroups+repeatscore+reviewscore*0.05+activescore+experiencescore+abroadinventoryscore*2+instock+iscompany+ciddmrsscore*0.2+catalogexternalscore*3+goodimage_fi+brandphone_fi*50)+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("105008", flumla105008);

        CustomFlumla flumla105C = new CustomFlumla("cellphone2", "score*tscore*(cidspecial_fi*10+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+brandscore_fi*3+createdatescore*3*iscompany+cidprice_fi+historyprice_fi+leadingtime_fi+picscore+repeatscore+reviewscore*0.05+activescore+experiencescore+abroadinventoryscore*2+instock+iscompany+ciddmrsscore*0.2+disputerate_fi+refundrate_fi+salesvolume_fi+overseasStorage_fi*5+goodimage_fi)+businesscore+dmrslevelscore_ii+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("92", flumla105C);   //跟90对比

        //http://www.dhgate.com/manufacturers/wedding-dress-suppliers.html
        CustomFlumla flumla91 = new CustomFlumla("manufacturers", "tscore*1.1306+goodimage_fi*1.3+gmvscore*0.4566+newkaseller_fi*0.4566+oldkaseller_fi*0.4566+picscore*0.0396+dmrslevelscore_ii*0.0238+profeedbackscore*0.0302+historyprice_fi*0.0773+cidprice_fi*0.1101+leadingtime_fi*0.0095+sincerity*0.1995+freeshippingscore*0.2794+reviewtimescore*0.0935+reviewscore*0.0354+allf+tnslevel");
        scoreServer.addFlumla("91", flumla91);   //对比用，新算法后缀从B开始

        /**
         wedding行业移动端：
         1.	增加新品加权因子，新品因子在企业卖家上生效；
         2.	移动专享价因子
         3.	同时增加不良体验率和卖家活跃度因素；
         4.	排序公式仅在用户过滤wedding类目时生效。
         类目id：002 全部上 (2016-05-26发版  2016-06-29发新版M002 ，注释掉这里)
         */
        CustomFlumla flumla002 = new CustomFlumla("wedding002", "score*tscore*(cidspecial_fi*30+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+picscore+wdnewproduct_fi*7+catalogexternalscore*5+salesvolume_fi*2)+businesscore+experiencescore+activescore+cidf+allf+conversiontype_fi*7+goodimage_fi+tnslevel");
        scoreServer.addFlumla("002", flumla002);
        CustomFlumla flumla002009 = new CustomFlumla("wedding002009", "score*tscore*(cidspecial_fi*30+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+picscore+createdatescore*7*iscompany+catalogexternalscore*3.5)+businesscore+dmrslevelscore_ii+experiencescore+activescore+cidf+allf+conversionrate*7+goodimage_fi+tnslevel");
        scoreServer.addFlumla("002009", flumla002009);
        CustomFlumla flumla002016 = new CustomFlumla("wedding002016", "score*tscore*(gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+picscore+createdatescore*7*iscompany+catalogexternalscore*4.5)+businesscore+dmrslevelscore_ii+experiencescore+activescore+cidf+allf+conversionrate*7+goodimage_fi+tnslevel");
        scoreServer.addFlumla("002016", flumla002016);
        CustomFlumla flumla002013 = new CustomFlumla("wedding002013", "score*tscore*(gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+picscore+createdatescore*7*iscompany+catalogexternalscore*3.2)+businesscore+dmrslevelscore_ii+experiencescore+activescore+cidf+allf+conversionrate*7+goodimage_fi+tnslevel");
        scoreServer.addFlumla("002013", flumla002013);
        CustomFlumla flumla002002 = new CustomFlumla("wedding002002", "score*tscore*(gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+picscore+createdatescore*7*iscompany+catalogexternalscore*4)+businesscore+dmrslevelscore_ii+experiencescore+activescore+cidf+allf+conversionrate*7+goodimage_fi+tnslevel");
        scoreServer.addFlumla("002002", flumla002002);
        CustomFlumla flumlaM002 = new CustomFlumla("wedding002", "score*tscore*(cidspecial_fi*30+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+picscore+createdatescore*7*iscompany+catalogexternalscore*5+ mobilepromrank_fi*2.5)+businesscore+dmrslevelscore_ii+experiencescore+activescore+goodimage_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("M002", flumlaM002);
        CustomFlumla flumlaM002009 = new CustomFlumla("wedding002009", "score*tscore*(cidspecial_fi*30+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+picscore+createdatescore*7*iscompany+catalogexternalscore*3.5+ mobilepromrank_fi*2.5)+businesscore+dmrslevelscore_ii+experiencescore+activescore+goodimage_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("M002009", flumlaM002009);
        CustomFlumla flumlaM002016 = new CustomFlumla("wedding002016", "score*tscore*(gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+picscore+createdatescore*7*iscompany+catalogexternalscore*4.5+ mobilepromrank_fi*2.5)+businesscore+dmrslevelscore_ii+experiencescore+activescore+cidf+allf+conversionrate*7+goodimage_fi+tnslevel");
        scoreServer.addFlumla("M002016", flumlaM002016);
        CustomFlumla flumlaM002013 = new CustomFlumla("wedding002013", "score*tscore*(gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+picscore+createdatescore*7*iscompany+catalogexternalscore*3.2+ mobilepromrank_fi*2.5)+businesscore+dmrslevelscore_ii+experiencescore+activescore+cidf+allf+conversionrate*7+goodimage_fi+tnslevel");
        scoreServer.addFlumla("M002013", flumlaM002013);
        CustomFlumla flumlaM002002 = new CustomFlumla("wedding002002", "score*tscore*(gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+picscore+createdatescore*7*iscompany+catalogexternalscore*4+ mobilepromrank_fi*2.5)+businesscore+dmrslevelscore_ii+experiencescore+activescore+cidf+allf+conversionrate*7+goodimage_fi+tnslevel");
        scoreServer.addFlumla("M002002", flumlaM002002);

        /** 母婴和玩具类目增加新品因子加权--catalogid：102,111
         1.	产品上传时间（30%）
         2.	新上传产品近一个月Sold订单数量大于等于2单（25%）
         3.	商品销售起批量大于等于1 lot或大于等于5 Pieces（20%）
         4.	GMV（15%）
         5.	T/P级卖家（10%）
         TODO:wholesaleonlyexternalscore 修改为 wholesalescore字段。wholesaleonlyexternalscore是临时测试用
         */
        CustomFlumla flumla81 = new CustomFlumla("toy", "score * tscore * (gmvscore * 3+newkaseller_fi*3+oldkaseller_fi*3 + gmvscore * createdatescore * 5 + " +
                "createdatescore * 6  + ciddmrsscore * 2 + wholesalescore * 4 + goodimage_fi)"
                + " + cidf + allf + tnslevel ");
        scoreServer.addFlumla("81", flumla81);

        CustomFlumla flumla024059 = new CustomFlumla("apparel", "score*tscore*(cidspecial_fi*10+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi+dhlinkmodel_fi+fpc+fpa+brandscore_fi*3)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+goodimage_fi+tnslevel");
        scoreServer.addFlumla("024059", flumla024059);

        //baby girls shoes 只有TP级卖家新品进行加权 (类目id:  111006002) （2016-05-18 84）
        CustomFlumla flumla84 = new CustomFlumla("shoes", "score * tscore * (gmvscore * 3+newkaseller_fi*3+oldkaseller_fi*3 + gmvscore * createdatescore * 5 + " +
                " createdatescore * 6 * ciddmrsscore + ciddmrsscore * 2  + wholesalescore * 4)"
                + " + cidf + allf + tnslevel ");
        scoreServer.addFlumla("84", flumla84);

        //baby girls dress 突出新品因子，去除卖家级别 (类目id： 111006005  111003033) （2016-05-18 85）
        CustomFlumla flumla85 = new CustomFlumla("girlsdress", "score * tscore * (gmvscore * 3+newkaseller_fi*3+oldkaseller_fi*3 + gmvscore * createdatescore * 5 + " +
                " createdatescore * 3 + wholesalescore * 4+goodimage_fi)"
                + " + cidf + allf + tnslevel ");
        scoreServer.addFlumla("85", flumla85);

        //111003033  增加品牌因素 2016-07-04
        CustomFlumla flumla111003033 = new CustomFlumla("baby", "score * tscore * (gmvscore * 3+newkaseller_fi*3+oldkaseller_fi*3 + gmvscore * createdatescore * 5 + createdatescore * 3 + wholesalescore * 4+ brandscore_fi*5 + goodimage_fi) + cidf + allf + tnslevel");
        scoreServer.addFlumla("111003033", flumla111003033);
        //111003033  增加品牌因素 2016-07-04
        CustomFlumla flumla111003002 = new CustomFlumla("baby", "score * tscore * (gmvscore * 3+newkaseller_fi*3+oldkaseller_fi*3 + gmvscore * createdatescore * 5 + createdatescore * 6 + ciddmrsscore * 2 + wholesalescore * 4 + brandscore_fi*5 + goodimage_fi) + cidf + allf + tnslevel");
        scoreServer.addFlumla("111003002", flumla111003002);

        //家居行业个性化排序优化  增加品牌因素 019019 2016-07-04
        CustomFlumla flumla019019 = new CustomFlumla("home", "score*tscore*(gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi*3+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi+dhlinkmodel_fi+fpc+fpa+brandscore_fi*4)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+goodimage_fi+tnslevel");
        scoreServer.addFlumla("019019", flumla019019);

        //假发行业 一级类目（2016-05-18 86）
        //产品维度的买家重复购买率   当前类目产品GMV   卖家活跃度因素   买家不良体验率
        //假发行业 一级类目（2016-06-13 调整公式）
        CustomFlumla flumla86 = new CustomFlumla("hairBestMatch", "score*tscore*(gmvscore*6+newkaseller_fi*5+oldkaseller_fi*5+brandscore_fi*3+categroups+gmvscore*conversionrate+picscore+createdatescore*4*iscompany+ ciddmrsscore*2+reviewscore*0.1+repeatscore+disputerate_fi+refundrate_fi+salesvolume_fi+dhlinkmodel_fi+experiencescore+activescore+goodimage_fi)+businesscore+dmrslevelscore_ii+cidf+allf+tnslevel");
        scoreServer.addFlumla("86", flumla86);

//		//B类买家ppc导流 （2016-05-18 87）
//		//增加B类产品因素，增加wholesale因素，增加repeat因素，增加跨类目tps评级因素 
//		CustomFlumla flumla87 = new CustomFlumla("BBuyBestMatch", "gmvscore*0.2804+reviewscore*0.0106+activescore*0.0558+brandscore_fi*0.0125+categroups*0.3351+ciddmrsscore*0.002+cidprice_fi*0.0274+clickscore*0.0118+conversionrate*0.1145+createdatescore*0.1431+dhlinkmodel_fi*0.0288+disputerate_fi*0.0099+disputescore*0.3844+dmrsscore*0.0388+experiencescore*0.1171+freeshippingscore*0.1746+historyprice_fi*0.0166+isbcpro*0.1546+iscompany*0.0132+leadingtime_fi*0.0826+orderscore*0.0269+picscore*0.0075+productchannelscore*0.0806+profeedbackscore*0.0546+refundrate_fi*0.1742+refundscore*0.4044+repeatscore*0.316+reviewtimescore*0.0985+salesrate_fi*0.7485+salesvolume_fi*0.4485+sellerlevelscore*0.0596+sincerity*0.0388+allf+tnslevel+googlepolicyexternalscore");
//		scoreServer.addFlumla("87", flumla87);

        //在新调整的平台算法基础上增加移动专享价加权 2016-06-15
        CustomFlumla flumlaM130 = new CustomFlumla("flumlaM130", "score*tscore*(gmvscore*6+newkaseller_fi*5+oldkaseller_fi*5+brandscore_fi*3+categroups+gmvscore*conversionrate+picscore+createdatescore*4*iscompany+ ciddmrsscore*2+reviewscore*0.1+repeatscore+disputerate_fi+refundrate_fi+mobilepromrank_fi+salesvolume_fi+dhlinkmodel_fi+experiencescore+activescore)+businesscore+dmrslevelscore_ii+cidf+goodimage_fi+allf+tnslevel");
        scoreServer.addFlumla("M130", flumlaM130);

        /**
         汽配行业个性化排序：
         1、增加新品因子
         2、增加30天订单因子
         3、调整买家评价因子权重
         4、增加移动专享价因子
         5、调整卖家评级因子权重
         整个112二级类目除去112002和112005是默认算法外，其他类目采用新算法   100%上线   2016-06-17 (周五) 11:11
         */
        //CustomFlumla flumla112 = new CustomFlumla("flumla112", "score*tscore*(gmvscore*5+cidprice_fi+historyprice_fi+ createdatescore * 5 +ciddmrsscore+reviewscore*0.1+categroups+picscore+repeatscore+mobilepromrank_fi+salesrate_fi+salesvolume_fi*2+dhlinkmodel_fi+activescore+fpc+fpa)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        /** 汽配行业个性化排序二次调优 1、增加品牌因素 2016-07-04 */
        CustomFlumla flumla112 = new CustomFlumla("flumla112", "score*tscore*(cidspecial_fi*10+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi+ createdatescore * 5 +ciddmrsscore+reviewscore*0.1+categroups+picscore+repeatscore+mobilepromrank_fi+salesrate_fi+salesvolume_fi*2+dhlinkmodel_fi+activescore+fpc+fpa+brandscore_fi*5)+businesscore+dmrslevelscore_ii+leadingtime_fi+goodimage_fi+cidf+allf+conversionrate*7+goodimage_fi+tnslevel");
        scoreServer.addFlumla("112", flumla112);

        /** BHT移动端行业个性化排序 **/
        CustomFlumla flumlaBHT_A = new CustomFlumla("BHT_A", "score * tscore * (gmvscore * 3+newkaseller_fi*3+oldkaseller_fi*3 + gmvscore * createdatescore * 5 + createdatescore * 6  + ciddmrsscore * 2 + mobilepromrank_fi*2 + wholesalescore * 4+goodimage_fi) + cidf + allf + tnslevel");
        scoreServer.addFlumla("BHT_A", flumlaBHT_A);
        CustomFlumla flumlaBHT_B = new CustomFlumla("BHT_B", "score * tscore * (gmvscore * 3+newkaseller_fi*3+oldkaseller_fi*3 + gmvscore * createdatescore * 5 + createdatescore * 3+ mobilepromrank_fi*2 + wholesalescore * 4+goodimage_fi) + cidf + allf + tnslevel");
        scoreServer.addFlumla("BHT_B", flumlaBHT_B);
        CustomFlumla flumlaBHT_C = new CustomFlumla("BHT_C", "score * tscore * (gmvscore * 3+newkaseller_fi*3+oldkaseller_fi*3 + gmvscore * createdatescore * 5 + createdatescore * 6 * ciddmrsscore + ciddmrsscore * 2 + mobilepromrank_fi*2 + wholesalescore * 4+goodimage_fi) + cidf + allf + tnslevel");
        scoreServer.addFlumla("BHT_C", flumlaBHT_C);

        /** 健康美容行业个性化排序 **/
        CustomFlumla flumla018 = new CustomFlumla("018", "score*tscore*(cidspecial_fi*10+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi+reviewscore*0.1+picscore+repeatscore+brepeatscore+ciddmrsscore*0.5+createdatescore*ciddmrsscore+categroups+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi*2+experiencescore+activescore+fpc+fpa+goodimage_fi)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("018", flumla018);
        CustomFlumla flumlaM018 = new CustomFlumla("M018", "score*tscore*(cidspecial_fi*10+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi+reviewscore*0.1+picscore+repeatscore+brepeatscore+ciddmrsscore*0.5+createdatescore*ciddmrsscore+categroups+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi*2+experiencescore+activescore+fpc+fpa+goodimage_fi)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel+mobilepromrank_fi*2.5");
        scoreServer.addFlumla("M018", flumlaM018);//2016-08-30


        /** 相机行业个性化排序 **/
        CustomFlumla flumla106 = new CustomFlumla("106", "score*tscore*(cidspecial_fi*10+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+brandscore_fi*5+cidprice_fi+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi*5+dhlinkmodel_fi+fpc+fpa + ciddmrsscore + categroups + repeatscore * 5 + catalogexternalscore*5+goodimage_fi)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("106", flumla106);
        CustomFlumla flumlaM106 = new CustomFlumla("M106", "score*tscore*(cidspecial_fi*10+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+brandscore_fi*5+cidprice_fi+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi*5+dhlinkmodel_fi+fpc+fpa + ciddmrsscore + categroups + repeatscore * 5 + mobilepromrank_fi*2.5 + catalogexternalscore*5+goodimage_fi)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("M106", flumlaM106);
        CustomFlumla flumla103029 = new CustomFlumla("103029", "score*tscore*(gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+brandscore_fi*3+cidprice_fi+historyprice_fi+leadingtime_fi+picscore+categroups+repeatscore+reviewscore*0.05+activescore+experiencescore+abroadinventoryscore*2+instock+iscompany+ciddmrsscore*0.2+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi+dhlinkmodel_fi+wholesalescore+mobilepromrank_fi+brepeaterate+goodimage_fi)+businesscore+dmrslevelscore_ii+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("103029", flumla103029);
        CustomFlumla flumlaM103029 = new CustomFlumla("M103029", "score*tscore*(gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+brandscore_fi*3+cidprice_fi+historyprice_fi+leadingtime_fi+picscore+categroups+repeatscore+reviewscore*0.05+activescore+experiencescore+abroadinventoryscore*2+instock+iscompany+ciddmrsscore*0.2+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi+dhlinkmodel_fi+wholesalescore+mobilepromrank_fi*2.5+brepeaterate+goodimage_fi)+businesscore+dmrslevelscore_ii+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("M103029", flumlaM103029);
        CustomFlumla flumla103030 = new CustomFlumla("103030", "score*tscore*(gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+brandscore_fi*3+cidprice_fi+historyprice_fi+createdatescore*2*iscompany+createdatescore*2*salesvolume_fi+createdatescore*ciddmrsscore*0.4+leadingtime_fi+picscore+repeatscore+reviewscore*0.1+iscompany+ciddmrsscore*0.2+disputerate_fi+refundrate_fi+salesvolume_fi+dhlinkmodel_fi+wholesalescore+mobilepromrank_fi+brepeaterate+goodimage_fi)+businesscore+dmrslevelscore_ii+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("103030", flumla103030);
        CustomFlumla flumlaM103030 = new CustomFlumla("M103030", "score*tscore*(gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+brandscore_fi*3+cidprice_fi+historyprice_fi+createdatescore*2*iscompany+createdatescore*2*salesvolume_fi+createdatescore*ciddmrsscore*0.4+leadingtime_fi+picscore+repeatscore+reviewscore*0.1+iscompany+ciddmrsscore*0.2+disputerate_fi+refundrate_fi+salesvolume_fi+dhlinkmodel_fi+wholesalescore+mobilepromrank_fi*2.5+brepeaterate+goodimage_fi)+businesscore+dmrslevelscore_ii+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("M103030", flumlaM103030);

        CustomFlumla flumla104 = new CustomFlumla("104", "score*0.02165+tscore*0.0433+goodimage_fi*0.05+gmvscore*0.2145+newkaseller_fi*0.2145+oldkaseller_fi*0.2145+reviewscore*0.0471+picscore*0.1497+dmrslevelscore_ii*0.0134+sellerlevelscore*0.1239+profeedbackscore*0.0329+conversionrate*0.1846+historyprice_fi*0.0446+cidprice_fi*0.1442+leadingtime_fi*0.3302+createdatescore*0.0295+iscompany*0.0233+experiencescore*0.1711+activescore*0.0274+repeatscore*0.1741+sincerity*0.1089+isbcpro*0.1849+categroups*0.1405+productchannelscore*0.0864+freeshippingscore*0.2593+refundscore*0.1936+reviewtimescore*0.0633+disputescore*0.168+orderscore*0.0144+clickscore*0.0042+catalogexternalscore*0.1+allf");
        scoreServer.addFlumla("104", flumla104);
        CustomFlumla flumlaM104 = new CustomFlumla("M104", "score*0.02165+tscore*0.0433+goodimage_fi*0.05+gmvscore*0.2145+newkaseller_fi*0.2145+oldkaseller_fi*0.2145+reviewscore*0.0471+picscore*0.1497+dmrslevelscore_ii*0.0134+sellerlevelscore*0.1239+profeedbackscore*0.0329+conversionrate*0.1846+historyprice_fi*0.0446+cidprice_fi*0.1442+leadingtime_fi*0.3302+createdatescore*0.0295+iscompany*0.0233+experiencescore*0.1711+activescore*0.0274+repeatscore*0.1741+sincerity*0.1089+isbcpro*0.1849+categroups*0.1405+productchannelscore*0.0864+freeshippingscore*0.2593+refundscore*0.1936+reviewtimescore*0.0633+disputescore*0.168+orderscore*0.0144+clickscore*0.0042+catalogexternalscore*0.1+allf+ mobilepromrank_fi * 0.10725");
        scoreServer.addFlumla("M104", flumlaM104);

        CustomFlumla flumla110 = new CustomFlumla("110", "score*tscore*(gmvscore*5+goodimage_fi+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi*3+dhlinkmodel_fi+fpc+fpa+iscompany+wholesalescore*2+createdatescore*3+catalogexternalscore*3)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("110", flumla110);

        CustomFlumla flumlaM110 = new CustomFlumla("M110", "score*tscore*(gmvscore*5+goodimage_fi+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi*3+dhlinkmodel_fi+fpc+fpa+iscompany+wholesalescore*2+createdatescore*3+catalogexternalscore*3+ mobilepromrank_fi*2.5)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("M110", flumlaM110);
        CustomFlumla flumlaM110002 = new CustomFlumla("M110002", "score*tscore*(gmvscore*5+goodimage_fi+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi+dhlinkmodel_fi+fpc+fpa+wholesalescore+iscompany+createdatescore*2+ mobilepromrank_fi*2.5)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("M110002", flumlaM110002);
        CustomFlumla flumlaM110003 = new CustomFlumla("M110003", "score*tscore*(gmvscore*5+goodimage_fi+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi+dhlinkmodel_fi+fpc+fpa+wholesalescore*2+iscompany*createdatescore*5+ mobilepromrank_fi*2.5)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("M110003", flumlaM110003);

        /**
         *	-----20160725-------
         手机-翻新手机个性化内容：
         1、增加纠纷率因素
         2、增加退款率因素
         3、增加30天订单因素
         4、调整卖家综合得分因素
         5、减小品牌因素权重
         展示类目：105003
         分流策略：五五分流
         生效范围：主站
         */
        CustomFlumla flumla105003 = new CustomFlumla("105003", "score*tscore*(gmvscore*5+goodimage_fi+newkaseller_fi*5+oldkaseller_fi*5+brandscore_fi+createdatescore*3*iscompany+cidprice_fi+historyprice_fi+leadingtime_fi+picscore+repeatscore+reviewscore*0.05+activescore+experiencescore+abroadinventoryscore*2+instock+iscompany+ciddmrsscore*0.2+disputerate_fi*1.5+refundrate_fi*1.5+salesvolume_fi*3)+businesscore+dmrslevelscore_ii+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("105003", flumla105003);

        /**
         * 	-----20160725-------
         手机行业-手机配件和可穿戴设备 个性化排序内容：
         考虑所有已知因素，向订单转化最大化方向优化；
         展示类目：105004和105012
         分流策略：五五分流
         生效范围：主站
         */
        CustomFlumla flumla105004 = new CustomFlumla("105004", "score*0.1678*0.5+tscore*0.1678+goodimage_fi*0.18+gmvscore*0.2611+newkaseller_fi*0.2611+oldkaseller_fi*0.2611+reviewscore*0.008+picscore*0.1116+dmrslevelscore_ii*0.0313+sellerlevelscore*0.0435+profeedbackscore*0.0104+conversionrate*0.3264+historyprice_fi*0.0081+cidprice_fi*0.1941+leadingtime_fi*0.0433+createdatescore*0.0484+iscompany*0.0965+experiencescore*0.1783+activescore*0.0146+repeatscore*0.1983+sincerity*0.0647+isbcpro*0.1655+categroups*0.0327+productchannelscore*0.0508+freeshippingscore*0.1803+refundscore*0.2733+reviewtimescore*0.0732+disputescore*0.2553+orderscore*0.0178+clickscore*0.0092+allf+tnslevel");
        scoreServer.addFlumla("105004", flumla105004);

        /**
         * 	-----20160726-------
         家具个性化排序，加入wholesale 和 企业卖家因子，需要进行5:5分流  移动的需要增加移动专享价
         */
        CustomFlumla flumla019 = new CustomFlumla("019", "score*tscore*(gmvscore*5+goodimage_fi+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi+dhlinkmodel_fi+fpc+fpa+ wholesalescore)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("019", flumla019);
        scoreServer.addFlumla("M019A", flumlaMdef);
        /**
         * 	-----20160726-------
         家具行业中的低级类目-画（019032016） 只加企业卖家因子
         家具行业中的低级类目-水烟（019019001004）   移动的需要增加移动专享价
         */
        CustomFlumla flumla019032016 = new CustomFlumla("019032016", "score*tscore*(gmvscore*5+goodimage_fi+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi+dhlinkmodel_fi+fpc+fpa+ iscompany)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("019032016", flumla019032016);

        /**
         * 	-----20160802-------
         灯具行业（117004003：灯管，117002010：投光灯）个性化排序，增加海外仓因子，移动平台保留移动专享价，全流量上线
         */
        CustomFlumla flumla117004003 = new CustomFlumla("117004003", "score*tscore*(gmvscore*5+goodimage_fi+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi+dhlinkmodel_fi+fpc+fpa+overseasStorage_fi*5)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("117004003", flumla117004003);
        CustomFlumla flumlaM117004003 = new CustomFlumla("M117004003", "score*tscore*(gmvscore*5+goodimage_fi+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi+dhlinkmodel_fi+fpc+fpa+overseasStorage_fi*5+ mobilepromrank_fi*2.5)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("M117004003", flumlaM117004003);

        /**
         * 	-----20161129-------
         灯具行业（117004002 全流量上线)
         */
        CustomFlumla flumla117004002 = new CustomFlumla("117004002", "score*tscore*(gmvscore*5+goodimage_fi+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi+dhlinkmodel_fi+fpc+fpa+prominorder_fi*4)+businesscore+dmrslevelscore_ii+leadingtime_fi+cidf+allf+conversionrate*4+conversiontype_fi*7+tnslevel");
        scoreServer.addFlumla("117004002", flumla117004002);

        /**
         * 	-----20160815-------
         乐器行业（113）个性化排序，使用鹏飞逻辑回归公式，在此基础上，增大了新品因子，纠纷退款因子的权重，降低了行业价格因子权重。需要5:5分流测试
         */
        CustomFlumla flumla113 = new CustomFlumla("113", "score*0.05465+tscore*0.1093+goodimage_fi*0.13+gmvscore*0.0393+newkaseller_fi*0.0393+oldkaseller_fi*0.0393+reviewscore*0.0182+activescore*0.0504+brandscore_fi*0+categroups*0.5253+ciddmrsscore*0+cidprice_fi*0.1172+clickscore*1.596+conversionrate*0.0449+createdatescore*0.0466+dhlinkmodel_fi*0.0592+disputescore*0.1641+dmrslevelscore_ii*0.0024+experiencescore*0.1081+freeshippingscore*0.1346+historyprice_fi*0.0066+isbcpro*0.0564+iscompany*0.0544+leadingtime_fi*0.115+orderscore*0.3198+productchannelscore*0.18+profeedbackscore*0.0011+refundscore*0.1613+repeatscore*0.4056+reviewtimescore*0.0268+salesrate_fi*0.2729+sellerlevelscore*0.0126+sincerity*0.0839+allf+disputerate_fi*0.1+salesvolume_fi*0.2+refundrate_fi*0.1+picscore*0.1427");
        scoreServer.addFlumla("113", flumla113);

        /**
         * 	-----20160816-------
         珠宝行业（类目id：100）个性化排序，使用鹏飞回归的算法，在此基础上，加重了动销率权重，30天销售因子权重，新品权重，增加了一月内syi上传产品数量因子 。算法5:5分流
         */
        CustomFlumla flumla100 = new CustomFlumla("100", "score*0.0199+tscore*0.0398+goodimage_fi*0.05+gmvscore*0.1628+newkaseller_fi*0.1628+oldkaseller_fi*0.1628+reviewscore*0.0158+activescore*0.0646+brandscore_fi*0.0006+categroups*0.0677+ciddmrsscore*0+cidprice_fi*0.0186+clickscore*0.3459+conversionrate*0.011+dhlinkmodel_fi*0.0181+disputerate_fi*0.0307+disputescore*0.349+dmrslevelscore_ii*0.0304+experiencescore*0.0894+freeshippingscore*0.1246+historyprice_fi*0.0264+isbcpro*0.1145+iscompany*0.0038+leadingtime_fi*0.1888+orderscore*0.0547+picscore*0.0433+productchannelscore*0.1053+profeedbackscore*0.0278+refundrate_fi*0.1152+refundscore*0.3522+repeatscore*0.2429+reviewtimescore*0.0598+sellerlevelscore*0.028+sincerity*0.0839+allf+salesvolume_fi*0.6+syiToUploadCount_fi*0.3+salesrate_fi*0.6+createdatescore*0.2+tnslevel");
        scoreServer.addFlumla("100", flumla100);
        CustomFlumla flumlaM100 = new CustomFlumla("M100", "score*0.0199+tscore*0.0398+goodimage_fi*0.05+gmvscore*0.1628+newkaseller_fi*0.1628+oldkaseller_fi*0.1628+reviewscore*0.0158+activescore*0.0646+brandscore_fi*0.0006+categroups*0.0677+ciddmrsscore*0+cidprice_fi*0.0186+clickscore*0.3459+conversionrate*0.011+dhlinkmodel_fi*0.0181+disputerate_fi*0.0307+disputescore*0.349+dmrslevelscore_ii*0.0304+experiencescore*0.0894+freeshippingscore*0.1246+historyprice_fi*0.0264+isbcpro*0.1145+iscompany*0.0038+leadingtime_fi*0.1888+orderscore*0.0547+picscore*0.0433+productchannelscore*0.1053+profeedbackscore*0.0278+refundrate_fi*0.1152+refundscore*0.3522+repeatscore*0.2429+reviewtimescore*0.0598+sellerlevelscore*0.028+sincerity*0.0839+allf+salesvolume_fi*0.6+syiToUploadCount_fi*0.3+salesrate_fi*0.6+createdatescore*0.2+tnslevel+ mobilepromrank_fi* 0.10725");
        scoreServer.addFlumla("M100", flumlaM100);


        /**
         * 	-----20160901-------
         秋季类目 baby行业：类目id：111 增加新公式   五五分流
         */
        CustomFlumla flumla111 = new CustomFlumla("111", "score*tscore*(cidspecial_fi*10+goodimage_fi+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi*2+dhlinkmodel_fi+catalogexternalscore*5+createdatescore*5+fpc+fpa)+businesscore+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("111", flumla111);
        CustomFlumla flumlaM111 = new CustomFlumla("M111", "score*tscore*(cidspecial_fi*10+goodimage_fi+gmvscore*5+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi*2+dhlinkmodel_fi+catalogexternalscore*5+createdatescore*5+fpc+fpa+mobilepromrank_fi*2.5)+businesscore+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("M111", flumlaM111);

        /**
         * 	-----20160901-------
         秋季类目  配饰行业：类目id：109 增加新公式   五五分流
         */
        CustomFlumla flumla109 = new CustomFlumla("109", "score*tscore*(gmvscore*5+goodimage_fi+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi+dhlinkmodel_fi+catalogexternalscore*5+createdatescore*3+fpc+fpa)+businesscore+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("109", flumla109);
        CustomFlumla flumlaM109 = new CustomFlumla("M109", "score*tscore*(gmvscore*5+goodimage_fi+newkaseller_fi*5+oldkaseller_fi*5+cidprice_fi+historyprice_fi*3+reviewscore*0.1+picscore+repeatscore+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi+dhlinkmodel_fi+catalogexternalscore*5+createdatescore*3+fpc+fpa+mobilepromrank_fi*2.5 )+businesscore+leadingtime_fi+cidf+allf+conversionrate*7+tnslevel");
        scoreServer.addFlumla("M109", flumlaM109);

        //移动editor picks
        CustomFlumla mobilead = new CustomFlumla("mobilead", "score*8+gmvscore*5+goodimage_fi*5+reviewscore*0.1+disputerate_fi+refundrate_fi+salesrate_fi+salesvolume_fi+createdatescore*8+mobilepromrank_fi*2.5+conversionrate*7+tnslevel");
        scoreServer.addFlumla("mobilead", mobilead);

        //英语系国家UK维度算法
        CustomFlumla flumlaseouk = new CustomFlumla("seouk", "tscore*score*(goodimage_fi*4+conversionrate+createdatescore*2+en_gmv_uk_1_fi+en_gmv_uk_2_fi+en_gmv_uk_3_fi+gmvscore)+tnslevel");
        scoreServer.addFlumla("seouk", flumlaseouk);

        //新人优惠券引流
        CustomFlumla flumlacoupon = new CustomFlumla("newpack", "tscore*score*(goodimage_fi*4+conversionrate+createdatescore*2+gmvscore)+tnslevel");
        scoreServer.addFlumla("newpack", flumlacoupon);

        CustomFlumla couplist = new CustomFlumla("couplist", "score*2+tscore*0.2323+gmvscore*0.1946+reviewscore*0.0101+categroups*0.0795+cidprice_fi*0.0571+clickscore*0.0406+conversionrate*0.0877+createdatescore*0.0402+dhlinkmodel_fi*0.0143+disputerate_fi*0.0408+disputescore*0.3693+experiencescore*0.1543+freeshippingscore*0.128+historyprice_fi*0.0621+isbcpro*0.0872+iscompany*0.0375+leadingtime_fi*0.0764+orderscore*0.051+picscore*0.0508+productchannelscore*0.0291+profeedbackscore*0.1524+refundrate_fi*0.165+refundscore*0.3818+repeatscore*0.1801+reviewtimescore*0.2747+salesrate_fi*0.7607+salesvolume_fi*0.5243+sellerlevelscore*0.0481+sincerity*0.0246+goodimage_fi*0.1813+tnslevel");
        scoreServer.addFlumla("couplist", couplist);
        CustomFlumla seocoupon = new CustomFlumla("seocoupon", "score*16+createdatescore*5+tnslevel");
        scoreServer.addFlumla("seocoupon", seocoupon);
        CustomFlumla seorecentlysold = new CustomFlumla("seorecentlysold", "createdatescore*20+gmvscore+goodimage_fi+tnslevel");
        scoreServer.addFlumla("seorecentlysold", seorecentlysold);
        CustomFlumla onlyScore = new CustomFlumla("onlyScore", "score");
        scoreServer.addFlumla("onlyScore", onlyScore);

        for (int i = 0; i < 10; i++) {
            Thread.sleep(3000);
            Set<String> flumlaIds = scoreServer.getFlumlaIds();
            System.err.println("flumlaIds.size:" + flumlaIds.size());
        }

    }
}
