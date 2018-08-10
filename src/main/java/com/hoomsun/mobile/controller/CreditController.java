package com.hoomsun.mobile.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hoomsun.mobile.tools.ConstantInterface;
import com.hoomsun.mobile.tools.SimpleHttpClient;

import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("credit")
public class CreditController {
	
	  private static Logger logger = LoggerFactory.getLogger(CreditController.class);
	
	  	@ApiOperation(value = "征信")
	    @ResponseBody
	    @RequestMapping(value = "query",method = RequestMethod.POST)
	    public Map<String,Object> getMobileMethod(HttpServletRequest request) {
	  		String reportHtml = request.getParameter("reportHtml");
	  		logger.warn("---------reportHtml---"+reportHtml);
	  		String idCard = request.getParameter("idCard");
	  		logger.warn("---------idCard---"+idCard);
	    	return CreditController.analysisCredit(reportHtml,idCard);
	    }
	  	
	  	
	    public static Map<String, Object> analysisCredit(String reportHtml,String userId){

	        //解析结果信息
	        Map<String, Object> resultDataMap = new HashMap<>();
	        logger.warn("开始解析征信数据...");
	        
	        logger.warn("------征信：reportHtml" + reportHtml);
	        Elements table = null;
	        Document parse = null;
//	        String userId = "**************271X";
	        try {
//	            parse = Jsoup.parse(new File("f://xinzhengxin.html"), "utf-8");
	            parse = Jsoup.parse(reportHtml,"utf-8");
	            table = parse.getElementsByTag("table");
	            logger.warn(userId.toString() + "此次解析征信页面含有的table数量为:" + table.size());
	        } catch (Exception e) {
	            logger.error("征信数据解析失败，页面无法转换为正常html页面...",e);
	        }
	        //推送数据包;
	        JSONObject resultData = new JSONObject();
	        //数据集合
	        JSONObject resultObj = new JSONObject();
	        //征信报告基本信息
	        JSONObject creditBasic = new JSONObject();
	        //征信报告概要
	        JSONObject creditSummary = new JSONObject();
	        //征信报告信用卡信息
	        JSONArray creditCard = new JSONArray();
	        //征信报告贷款信息(住房贷款信息，其他信息)
	        JSONArray creditLoan = new JSONArray();
	        //征信报告担保信息
	        JSONArray creditGuarantee = new JSONArray();
	        //机构查询信息
	        JSONArray orgQueryData = new JSONArray();
	        //机构查询信息
	        JSONArray personQueryData = new JSONArray();
	        //页面中信息概要是第N个table
	        int mesGaiYao=7;
	        //页面机构查询是第N个table
	        int goverSelect=10;
	        //页面个人查询是第N个table
	        int proSelect=11;
	        //判断页面是否含有特殊表格   正常页面为13个table，特殊页面多一个保证人代偿信息table，需做特殊处理
	        if(parse.text().contains("保证人代偿信息")&&parse.text().contains("资产处置信息")){
	            mesGaiYao=8;
	            goverSelect=11;
	            proSelect=12;
	        }
	        try {
	            //读取页面中第二个表格中关于征信报告的基本信息
	            Element tbBasic1 = table.get(1);
	            Elements trBasic1 = tbBasic1.getElementsByTag("tr");
	            String flagReport = "个人信用报告";
	            if (flagReport.equals(trBasic1.get(0).text())) {
	                Elements tdBasic1 = trBasic1.get(1).getElementsByTag("td");
	                //报告编号
	                creditBasic.put("order_id", tdBasic1.get(0).text().substring(5).replace(" ", ""));
	                //查询时间
	                creditBasic.put("selectTime", tdBasic1.get(1).text().substring(5));
	                creditSummary.put("query_date", tdBasic1.get(1).text().substring(5));
	                //报告时间
	                creditBasic.put("reportTime", tdBasic1.get(2).text().substring(5));
	            } else {
	                logger.warn("页面第2个表格非个人信用报告...");
	            }
	            //获取征信报告姓名、证件类型、证件号码
	            Element tbBasic2 = table.get(2);
	            Elements trBasic2 = tbBasic2.getElementsByTag("tr");
	            String nameFlag = "姓名：";
	            Elements tdBasic2 = trBasic2.get(0).getElementsByTag("td");
	            if (tdBasic2.get(0).text().contains(nameFlag)) {
	                //报告姓名
	                creditBasic.put("name", tdBasic2.get(0).text().substring(3).trim());
	                //证件类型
	                creditBasic.put("type", tdBasic2.get(1).text().substring(5));
	                //证件号码
	                creditBasic.put("idnumber", tdBasic2.get(2).text().substring(5));
	                //证件空白
	                creditBasic.put("kongbai", "");
	            } else {
	                logger.warn("页面第3个表格非个人信用报告...");
	            }
	            resultObj.put("credit_basic", creditBasic);
	        } catch (Exception e) {
	            logger.error("获取个人信用报告失败", e);
	        }

	        try {
	            Element summaryTable = table.get(mesGaiYao);
	            //获取征信报告概要
	            Elements trSummary = summaryTable.getElementsByTag("tr");
	            String tdTitle = trSummary.get(0).text();
	            String keyWord1 = "信用卡";
	            String keyWord2 = "购房贷款";
	            String keyWord3 = "其他贷款";
	            if (tdTitle.contains(keyWord1) && tdTitle.contains(keyWord2) && tdTitle.contains(keyWord3)) {
	                //账户数信息
	                Elements tdSummary1 = trSummary.get(1).getElementsByTag("td");
	                //信用卡账户数
	                creditSummary.put("card_account", tdSummary1.get(1).text());
	                //购房贷款账户数
	                creditSummary.put("housing_loan_account", tdSummary1.get(2).text());
	                //其他贷款账户数
	                creditSummary.put("other_loan_account", tdSummary1.get(3).text());

	                //未结清/未销户账户数
	                Elements tdSummary2 = trSummary.get(2).getElementsByTag("td");
	                //信用卡未结清/未销户账户数
	                creditSummary.put("card_notsettled", tdSummary2.get(1).text());
	                //购房贷款未结清/未销户账户数
	                creditSummary.put("housing_loan_notsettled", tdSummary2.get(2).text());
	                //其他贷款未结清/未销户账户数
	                creditSummary.put("other_loan_notsettled", tdSummary2.get(3).text());

	                //发生过逾期的账户数
	                Elements tdSummary3 = trSummary.get(3).getElementsByTag("td");
	                //信用卡发生过逾期的账户数
	                creditSummary.put("card_overdue", tdSummary3.get(1).text());
	                //购房贷款发生过逾期的账户数
	                creditSummary.put("housing_loan_overdue", tdSummary3.get(2).text());
	                //其他贷款发生过逾期的账户数
	                creditSummary.put("other_loan_overdue", tdSummary3.get(3).text());

	                // 发生过90天以上逾期的账户数
	                Elements tdSummary4 = trSummary.get(4).getElementsByTag("td");
	                //信用卡发生过90天以上逾期的账户数
	                creditSummary.put("card_90overdue", tdSummary4.get(1).text());
	                //购房贷款发生过90天以上逾期的账户数 
	                creditSummary.put("housing_loan_90overdue", tdSummary4.get(2).text());
	                //其他贷款发生过90天以上逾期的账户数
	                creditSummary.put("other_loan_90overdue", tdSummary4.get(3).text());

	                // 为他人担保笔数
	                Elements tdSummary5 = trSummary.get(5).getElementsByTag("td");
	                //信用卡为他人担保笔数
	                creditSummary.put("card_guaranty", tdSummary5.get(1).text());
	                //购房贷款为他人担保笔数
	                creditSummary.put("housing_loan_guaranty", tdSummary5.get(2).text());
	                //其他贷款为他人担保笔数
	                creditSummary.put("other_loan_guaranty", tdSummary5.get(3).text());
	            } else {
	                logger.warn("没有获取到正确的征信报告概要");
	                
	                //信用卡账户数
	                creditSummary.put("card_account", "0");
	                //购房贷款账户数
	                creditSummary.put("housing_loan_account", "0");
	                //其他贷款账户数
	                creditSummary.put("other_loan_account", "0");

	                //信用卡未结清/未销户账户数
	                creditSummary.put("card_notsettled", "0");
	                //购房贷款未结清/未销户账户数
	                creditSummary.put("housing_loan_notsettled", "0");
	                //其他贷款未结清/未销户账户数
	                creditSummary.put("other_loan_notsettled","0");

	                //信用卡发生过逾期的账户数
	                creditSummary.put("card_overdue", "0");
	                //购房贷款发生过逾期的账户数
	                creditSummary.put("housing_loan_overdue", "0");
	                //其他贷款发生过逾期的账户数
	                creditSummary.put("other_loan_overdue", "0");

	                //信用卡发生过90天以上逾期的账户数
	                creditSummary.put("card_90overdue", "0");
	                //购房贷款发生过90天以上逾期的账户数
	                creditSummary.put("housing_loan_90overdue","0");
	                //其他贷款发生过90天以上逾期的账户数
	                creditSummary.put("other_loan_90overdue", "0");

	                //信用卡为他人担保笔数
	                creditSummary.put("card_guaranty","0");
	                //购房贷款为他人担保笔数
	                creditSummary.put("housing_loan_guaranty", "0");
	                //其他贷款为他人担保笔数
	                creditSummary.put("other_loan_guaranty", "0");
	                
//	                return CreditConstant.setErrorFailMap("0006", "征信报告概要获取失败");
	            }
	            resultObj.put("credit_summary", creditSummary);
	        } catch (Exception e) {
	            logger.error("征信概要解析失败", e);
	        }

	        try {
	            Elements ol = parse.getElementsByTag("span");
	            for (Element el : ol) {
	                //获取信用卡明细
	                if ("信用卡".equals(el.text())) {
	                    List<String> cardList = new ArrayList<>();
	                    Element element = el.nextElementSibling();
	                    Elements li = element.getElementsByTag("li");
	                    for (Element item : li) {
	                        cardList.add(item.text());
	                    }
	                    creditCard = getCreditCardList(cardList);
	                }
	                //获取其他贷款明细
	                //获取购房贷款明细
	                if ("其他贷款".equals(el.text()) || "购房贷款".equals(el.text())) {
	                    List<String> otherLoan = new ArrayList<>();
	                    Element element = el.nextElementSibling();
	                    Elements li = element.getElementsByTag("li");
	                    for (Element item : li) {
	                        otherLoan.add(item.text());
	                    }
	                    //已结清贷款需要报告时间这个字段
	                    String reportTime = creditBasic.getString("reportTime");
	                    DateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	                    Date parse1 = simpleDateFormat.parse(reportTime);
	                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy年MM月dd日");
	                    String reportTimeNew = simpleDateFormat1.format(parse1);
	                    //解析贷款明细
	                    creditLoan = getOtherLoanList(otherLoan, el.text(), creditLoan,reportTimeNew);
	                }

	                //为他人担保信息
	                if ("为他人担保信息".equals(el.text())) {
	                    List<String> otherGuarantee = new ArrayList<>();
	                    Element element = el.nextElementSibling();
	                    Elements li = element.getElementsByTag("li");
	                    for (Element item : li) {
	                        otherGuarantee.add(item.text());
	                    }
	                    creditGuarantee = getCreditGuarantee(otherGuarantee);
	                }
	            }
	        } catch (Exception e) {
	            logger.warn("数据明细获取失败", e);
	        }
	        resultObj.put("credit_card", creditCard);
	        resultObj.put("credit_loan", creditLoan);
	        resultObj.put("credit_guarantee", creditGuarantee);
	        //公共记录
	        JSONArray publicData = new JSONArray();
	        JSONObject onePbData = new JSONObject();
	        onePbData.put("public_record", "公共记录");
	        publicData.add(onePbData);
	        resultObj.put("credit_ggjl", publicData);


	        try {
	            //机构查询明细
	            Element orgElement = table.get(goverSelect);
	            orgQueryData = analysisOrgQuery(orgElement, "org");
	            //个人查询明细
	            Element personElement = table.get(proSelect);
	            personQueryData = analysisOrgQuery(personElement, "per");
	        }  catch (Exception e) {
	            logger.error("数据明细获取失败", e);
//	            return CreditConstant.setErrorFailMap("0011", "查询明细信息解析失败");
	        }
	        resultObj.put("credit_chaxun1", orgQueryData);
	        resultObj.put("credit_chaxun2", personQueryData);
	        resultData.put("data", resultObj);
	        resultData.put("idcode", userId.toString());
	        resultData.put("html", parse.html());
	        System.out.println(resultData);
	        try {
	            Map<String,Object> postData=new HashMap<>();
	            postData.put("data",resultData.toString());
	            
	            logger.warn("..........解析征信数据...结束....开始往数据中心数据推送数据，数据为：" + JSONObject.fromObject(postData));
	            
	            String post = SimpleHttpClient.post(ConstantInterface.port+"/HSDC/person/creditInvestigation", postData, null);
	            logger.warn("..........结束往数据中心数据推送数据，结果为：" + post);
	            JSONObject jsonObject = JSONObject.fromObject(post);

	            if(jsonObject.get("errorCode").equals("0000")){
	                resultDataMap.put("errorCode","0000");
	                resultDataMap.put("errorInfo","查询成功");
	                
	            }else{
	                resultDataMap.put("errorCode",jsonObject.get("errorCode"));//异常处理
	                resultDataMap.put("errorInfo",jsonObject.get("errorInfo"));
	            }
	           
	        } catch (Exception e) {
	            logger.error("征信推送数据过程中出现异常",e);
	            resultDataMap.put("errorCode","0001");
                resultDataMap.put("errorInfo","系统繁忙");
	        }
	        return resultDataMap;
	    }

	    /**
	     * 解析信用卡账单列表
	     *
	     * @param cardList
	     * @return
	     */
	    public static JSONArray getCreditCardList(List<String> cardList) {
	        JSONArray jsonArray = new JSONArray();
	            for (String cardRecord : cardList) {
	                if (cardRecord != null && !cardRecord.isEmpty()) {
	                    JSONObject cardJson = new JSONObject();
	                    cardJson.put("overdue", "0");
	                    cardJson.put("five_years_overdue", "0");
	                    cardJson.put("ninety_days_overdue", "0");
	                    cardJson.put("overdue_money", "0");
	                    //卡类型/
	                    String a = cardRecord.substring(cardRecord.indexOf("发放的") + 3);
	                    String purpose = a.substring(0,a.indexOf("（"));
	                    cardJson.put("account_category", purpose);

	                    if ("贷记卡".equals(purpose)) {
	                        //判断是否有逾期
	                        if (cardRecord.contains("逾期状态")) {
	                            cardJson.put("overdue", "1");
	                            //读取五年内逾期月数
	                            if (cardRecord.contains("年内有") && cardRecord.contains("个月处于逾期状态")) {
	                                String counts = cardRecord.substring(cardRecord.indexOf("年内有") + 3, cardRecord.indexOf("个月处于逾期状态"));
	                                cardJson.put("five_years_overdue", counts);
	                            }
	                            //逾期超过90天次数
	                            if (cardRecord.contains("逾期超过90天")) {
	                                String yuQi90Count = cardRecord.substring(cardRecord.indexOf("其中") + 2, cardRecord.indexOf("个月逾期"));
	                                cardJson.put("ninety_days_overdue", yuQi90Count);
	                            }
	                            //当前逾期金额
	                            if (cardRecord.contains("逾期金额")) {
	                                String dueMoney = cardRecord.substring(cardRecord.indexOf("逾期金额") + 4);
	                                dueMoney = dueMoney.substring(0, dueMoney.indexOf("。")).replace(",", "").trim();
	                                cardJson.put("overdue_money", dueMoney);
	                            }
	                        }
	                    } else if ("准贷记卡".equals(purpose)) {
	                        //判断是否有透支超过60天记录（逾期）
	                        if (cardRecord.contains("透支超过60天")) {
	                            cardJson.put("overdue", "1");
	                            //读取五年内逾期月数
	                            if (cardRecord.contains("年内有") && cardRecord.contains("个月透支超过60天")) {
	                                String counts = cardRecord.substring(cardRecord.indexOf("年内有") + 3, cardRecord.indexOf("个月透支超过60天"));
	                                cardJson.put("five_years_overdue", counts);
	                            }
	                            //逾期超过90天次数
	                            if (cardRecord.contains("透支超过90天")) {
	                                String yuQi90Count = cardRecord.substring(cardRecord.indexOf("其中") + 2, cardRecord.indexOf("个月透支超过90天"));
	                                cardJson.put("ninety_days_overdue", yuQi90Count);
	                            }
	                        }
	                    } else {
	                        //系统暂无匹配类型的信用卡
	                    }

	                    // 正常、逾期、呆账、未激活、销户
	                    //帐户状态：未激活,销户, 正常，呆账
	                    if (cardRecord.indexOf("未激活") > 0) {
	                        cardJson.put("account_state", "未激活");
	                    } else if (cardRecord.indexOf("销户") > 0) {
	                        cardJson.put("account_state", "销户");
	                    } else if (cardRecord.indexOf("呆账") > 0) {
	                        cardJson.put("account_state", "呆账");
	                    } else if (cardRecord.indexOf("逾期金额") > 0) {
	                        cardJson.put("account_state", "逾期");
	                    } else {
	                        cardJson.put("account_state", "正常");
	                    }

	                    //发卡日期
	                    String cardTime = cardRecord.substring(0, cardRecord.indexOf("日") + 1);
	                    cardJson.put("grant_date", cardTime);
	                    //发卡行
	                    String bankName = cardRecord.substring(cardRecord.indexOf("日") + 1, cardRecord.indexOf("发放的"));
	                    cardJson.put("bank", bankName);
	                    //账户类型
	                    String accountType = cardRecord.substring(cardRecord.indexOf("（") + 1, cardRecord.indexOf("）"));
	                    cardJson.put("account_type", accountType);
	                    //截止日期
	                    String expireDateStr = cardRecord.substring(cardRecord.indexOf("截至") + 2);
	                    expireDateStr = expireDateStr.substring(0, expireDateStr.indexOf("月") + 1);
	                    cardJson.put("query_date", expireDateStr);
	                    //信用额度
	                    String creditLine = "0";
	                    if (cardRecord.contains("信用额度")) {
	                        if (cardRecord.contains("折合人民币")) {
	                            creditLine = cardRecord.substring(cardRecord.indexOf("折合人民币") + 5);
	                        } else {
	                            creditLine = cardRecord.substring(cardRecord.indexOf("信用额度") + 4);
	                        }
	                        
	                        int index = creditLine.indexOf("，");
	                        int index1 = creditLine.indexOf("。");
	                        
	                        if(index > index1) {
	                        	index = index1;
	                        }
	                        
	                        creditLine = creditLine.substring(0, index).replace(",", "").trim();
	                        cardJson.put("credit_Line", creditLine);
	                    } else {
	                        cardJson.put("credit_Line", creditLine);
	                    }

	                    //已使用额度
	                    if (cardRecord.contains("已使用额度") || cardRecord.contains("透支余额")) {
	                        String usedMoney = null;
	                        if (cardRecord.contains("已使用额度")) {
	                            usedMoney = cardRecord.substring(cardRecord.indexOf("已使用额度") + 5);
	                            if (usedMoney.contains("逾期金额")) {
	                                usedMoney = usedMoney.substring(0, usedMoney.indexOf("，")).replace(",", "").trim();
	                            } else {
	                                usedMoney = usedMoney.substring(0, usedMoney.indexOf("。")).replace(",", "").trim();
	                            }
	                        } else if (cardRecord.contains("透支余额")) {
	                            usedMoney = cardRecord.substring(cardRecord.indexOf("透支余额") + 4);
	                            usedMoney = usedMoney.substring(0, usedMoney.indexOf("。")).replace(",", "").trim();
	                            if ("0".equals(creditLine)) {
	                                int i = Integer.parseInt(creditLine);
	                                int j = Integer.parseInt(usedMoney);
	                                if (j - i > 0) {
	                                    cardJson.put("account_state", "逾期");
	                                    cardJson.put("overdue_money", j - i);
	                                }
	                            }
	                        }
	                        cardJson.put("used_line", usedMoney);
	                    } else {
	                        cardJson.put("used_line", "0");
	                    }
	                    jsonArray.add(cardJson);
	                }
	            }
	            return jsonArray;
	    }


	    /**
	     * 解析其他贷款信息和购房贷款信息
	     *
	     * @param otherLoan
	     * @return
	     */
	    public static JSONArray getOtherLoanList(List<String> otherLoan, String loanType, JSONArray otherLoanList,String reportTimeNew) {
	            for (String loadRecord : otherLoan) {
	                JSONObject oneLoanRecord = new JSONObject();
	                if (loadRecord != null && !loadRecord.isEmpty()) {
	                    //贷款类型
	                    oneLoanRecord.put("type", loanType);
	                    //发放日期
	                    String cardTime = loadRecord.substring(0, loadRecord.indexOf("日") + 1);
	                    oneLoanRecord.put("grant_date", cardTime);
	                    //发放机构
	                    String bankName = loadRecord.substring(loadRecord.indexOf("日") + 1, loadRecord.indexOf("发放的"));
	                    oneLoanRecord.put("institution", bankName);
	                    //发放金额
	                    String a = loadRecord.substring(loadRecord.indexOf("发放的") + 3);
	                    String creditLine = a.substring( 0, a.indexOf("（") - 1).replace(",", "").trim();
	                    oneLoanRecord.put("money", creditLine);
	                    //贷款用途
	                    String purpose = loadRecord.substring(loadRecord.indexOf("）") + 1, loadRecord.indexOf("，"));
	                    oneLoanRecord.put("account_category_main", purpose);
	                    //其他贷款备注
	                    oneLoanRecord.put("account_category", "");
	                    //
	                    //到期日期
	                    if (loadRecord.contains("日到期")) {
	                        String expireDate = loadRecord.substring(loadRecord.indexOf("，") + 1, loadRecord.indexOf("日到期") + 1);
	                        oneLoanRecord.put("expired_date", expireDate);
	                    } else  {
	                        oneLoanRecord.put("expired_date", "");
	                    }
	                    //截止日期
	                    if (loadRecord.contains("截至")) {
	                        String pDateStr = loadRecord.substring(loadRecord.indexOf("截至") + 2, loadRecord.indexOf("余额") - 1);
	                        oneLoanRecord.put("query_date", pDateStr);
	                    } else {
	                        oneLoanRecord.put("query_date", "");
	                    }
	                    //余额
	                    if (loadRecord.contains("余额")) {
	                        String remainderStr = loadRecord.substring(loadRecord.indexOf("余额") + 2).replace(",", "");
	                        if (loadRecord.contains("逾期金额")) {
	                            remainderStr = remainderStr.substring(0, remainderStr.indexOf("，")).replaceAll(",", "").trim();
	                        } else {
	                            remainderStr = remainderStr.substring(0, remainderStr.indexOf("。")).replaceAll(",", "").trim();
	                        }
	                        oneLoanRecord.put("figure", remainderStr);
	                    } else {
	                        oneLoanRecord.put("figure", "0");
	                    }

	                    //贷款状态
	                    if (loadRecord.contains("已结清")) {
	                        oneLoanRecord.put("loan_state", "已结清");
	                        String pDateStr = loadRecord.substring(loadRecord.indexOf("，") + 1, loadRecord.indexOf("已结清"));
	                        //已结清的贷款到期时间为截止日期
	                        oneLoanRecord.put("expired_date", pDateStr);
	                        //已结清贷款截止日期为报告时间
	                        oneLoanRecord.put("query_date", reportTimeNew);
	                    } else if (loadRecord.contains("已转出")) {
	                        oneLoanRecord.put("loan_state", "已转出");
	                        String pDateStr = loadRecord.substring(loadRecord.indexOf("，") + 1, loadRecord.indexOf("已转出"));
	                        //已结清的贷款到期时间为截止日期
	                        oneLoanRecord.put("expired_date", pDateStr);
	                        //已结清贷款截止日期为报告时间
	                        oneLoanRecord.put("query_date", reportTimeNew);
	                    } else if (loadRecord.contains("逾期金额")) {
	                        oneLoanRecord.put("loan_state", "逾期");
	                    } else if (loadRecord.contains("呆账")) {
	                        oneLoanRecord.put("loan_state", "呆账");
	                    } else {
	                        oneLoanRecord.put("loan_state", "正常");
	                    }

	                    //逾期状态
	                    oneLoanRecord.put("overdue", "0");
	                    oneLoanRecord.put("years5_overdue", "0");
	                    oneLoanRecord.put("days90_overdue", "0");
	                    oneLoanRecord.put("yuqi_money", "0");
	                    //判断是否有逾期
	                    if (loadRecord.contains("逾期状态")) {
	                        oneLoanRecord.put("overdue", "1");
	                        //读取五年内逾期月数
	                        if (loadRecord.contains("年内有") && loadRecord.contains("个月处于逾期状态")) {
	                            String counts = loadRecord.substring(loadRecord.indexOf("年内有") + 3, loadRecord.indexOf("个月处于逾期状态"));
	                            oneLoanRecord.put("years5_overdue", counts);
	                        }
	                        //逾期超过90天次数
	                        if (loadRecord.contains("逾期超过90天")) {
	                            String yuQi90Count = loadRecord.substring(loadRecord.indexOf("其中") + 2, loadRecord.indexOf("个月逾期"));
	                            oneLoanRecord.put("days90_overdue", yuQi90Count);
	                        }
	                        //当前逾期金额
	                        if (loadRecord.contains("逾期金额")) {
	                            String dueMoney = loadRecord.substring(loadRecord.indexOf("逾期金额") + 4);
	                            dueMoney = dueMoney.substring(0, dueMoney.indexOf("。")).replace(",", "").trim();
	                            oneLoanRecord.put("yuqi_money", dueMoney);
	                        }
	                    }
	                }

	                otherLoanList.add(oneLoanRecord);
	            }
	            return otherLoanList;
	    }

	    /**
	     * 解析为他人担保信息
	     *
	     * @param otherGuarantee
	     * @return
	     */
	    public static JSONArray getCreditGuarantee(List<String> otherGuarantee) {
	        JSONArray jsonGuarantee = new JSONArray();
	        	 for (String recorde : otherGuarantee) {
	                 JSONObject infoMes = new JSONObject();
	                 if (recorde != null && !recorde.isEmpty()) {
	                   System.out.println("==recorde"+recorde);
	                     //起始日期
	                     String startTime = recorde.substring(0, recorde.indexOf("，"));
	                     infoMes.put("grant_date", startTime);
	                     //被担保人
	                     String nameLoan = recorde.substring(recorde.indexOf("为") + 1, recorde.indexOf("（"));
	                     infoMes.put("name_loan", nameLoan);
	                     //证件类型
	                     String cardType = recorde.substring(recorde.indexOf("证件类型：") + 5);
	                     cardType = cardType.substring(0, cardType.indexOf("，"));
	                     infoMes.put("cardType", cardType);
	                     //证件后四位
	                     String cardLoan = recorde.substring(recorde.indexOf("）") - 4, recorde.indexOf("）"));
	                     infoMes.put("card_loan", cardLoan);
	                     //贷款发放机构
	                     String institution = recorde.substring(recorde.indexOf("）在") + 2, recorde.indexOf("办理的"));
	                     infoMes.put("institution", institution);
	                     //担保合同金额
	                     String dbMoney="0";
	                     String dbMoney2="0";
	                     if(recorde.indexOf("担保贷款合同金额")>-1){
	                     dbMoney = recorde.substring(recorde.indexOf("担保贷款合同金额") + 8);
	                     dbMoney = dbMoney.substring(0, dbMoney.indexOf("，")).replace(",", "");
	                  
	                     //担保金额
	                     dbMoney2 = recorde.substring(recorde.indexOf("担保金额") + 4);
	                     dbMoney2 = dbMoney2.substring(0, dbMoney2.indexOf("。")).replace(",", "");
	                 
	                     }
	                     else if(recorde.indexOf("授信额度")>-1){
	                          dbMoney = recorde.substring(recorde.indexOf("授信额度") + 4);
	                            dbMoney = dbMoney.substring(0, dbMoney.indexOf("，")).replace(",", "");
	                         
	                            //担保金额
	                            dbMoney2 = recorde.substring(recorde.indexOf("担保金额") + 4);
	                            dbMoney2 = dbMoney2.substring(0, dbMoney2.indexOf("。")).replace(",", "");
	                     }
	                       
	                     infoMes.put("money", dbMoney);
	                     infoMes.put("money2", dbMoney2);
	                     //截止日期
	                     String queryDate = recorde.substring(recorde.indexOf("截至") + 2);
	                     queryDate = queryDate.substring(0, queryDate.indexOf("，"));
	                     infoMes.put("query_date", queryDate);
	                     //余额
	                     String figure="0";
	                     if(recorde.indexOf("担保贷款余额")>-1){
	                       figure = recorde.substring(recorde.indexOf("担保贷款余额") + 6);
	                       figure = figure.substring(0, figure.indexOf("。")).replace(",", "");
	                     }
	                    
	                   
	                     infoMes.put("figure", figure);
	                     //贷款状态
	                     if (figure.equals("0")) {
	                         infoMes.put("loan_state", "已结清");
	                     } else {
	                         if (recorde.contains("逾期金额")) {
	                             infoMes.put("loan_state", "逾期");
	                         } else {
	                             infoMes.put("loan_state", "正常");
	                         }
	                     }
	                     //逾期金额
	                     infoMes.put("yuqi_money", "待确定");
	                 }
	                 jsonGuarantee.add(infoMes);
	             }
	             return jsonGuarantee;
	    }

	    /**
	     * 解析机构查询表单
	     *
	     * @param table
	     */
	    public static JSONArray analysisOrgQuery(Element table, String flag) {
	        JSONArray orgQueryData = new JSONArray();
	        String signle = "";
	            Elements tr = table.getElementsByTag("tr");
	            if (tr.size() > 2) {
	                String text = tr.get(0).text();

	                if (flag.equals("org")) {
	                    signle = "机构查询记录明细";
	                } else if (flag.equals("per")) {
	                    signle = "个人查询记录明细";
	                }
	                if (text.equals(signle)) {
	                    for (int i = 3; i < tr.size() - 1; i++) {
	                        JSONObject jsonData = new JSONObject();
	                        Elements td = tr.get(i).getElementsByTag("td");
	                        jsonData.put("chaxun_date", td.get(1).text());
	                        jsonData.put("caozuoyuan", td.get(2).text());
	                        jsonData.put("reason", td.get(3).text());
	                        orgQueryData.add(jsonData);
	                    }
	                } else {
	                    System.out.println(signle + "记录表格获取失败");
	                }
	            }
	            return orgQueryData;
	    }
	    
	    
	   /* public static void main(String[] args) {
			System.out.println(new CreditController().getMobileMethod(null, "<?xml version=\"1.0\" encoding=\"GBK\"?><html>\r\n" + 
					"<head><script type=\"text/javascript\">//<![CDATA[var _BASEPATH_='https://ipcrs.pbccrc.org.cn/';//]]></script><script src=\"https://ipcrs.pbccrc.org.cn/js/pe.js\" type=\"text/javascript\"></script><link rel=\"stylesheet\" type=\"text/css\" href=\"https://ipcrs.pbccrc.org.cn/css/report.css\"/><meta http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\"/><title>个人信用报告</title><script src=\"js/jquery.js\" type=\"text/javascript\"></script></head><body bgcolor=\"white\"><div align=\"center\"><div><table width=\"980\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-image: url(images/jspwatermark.png); background-repeat: repeat;\"><tbody><tr></tr><tr><td><table width=\"980\" border=\"0\" align=\"center\" cellspacing=\"0\"><tbody><tr><th colspan=\"6\" align=\"center\"><div align=\"center\" class=\"h1\"><strong>个人信用报告</strong></div></th></tr><tr><td width=\"377\" colspan=\"2\" align=\"left\"><strong class=\"p\">报告编号： 2018030800005224893147 </strong></td><td width=\"300\" colspan=\"2\" align=\"left\"><strong class=\"p\">查询时间：2018.03.07 21:58:37 </strong></td><td width=\"300\" colspan=\"2\" align=\"right\"><strong class=\"p\">报告时间：2018.03.08 10:49:39 </strong></td></tr></tbody></table><table width=\"980\" border=\"0\" align=\"center\" cellspacing=\"0\"><tbody><tr><td width=\"170\" align=\"left\"><strong class=\"p\">姓名： 张美娟 </strong></td><td width=\"167\" align=\"left\"><strong class=\"p\">证件类型：身份证 </strong></td><td width=\"269\" align=\"right\"><strong class=\"p\">证件号码：**************2121 </strong></td><td width=\"133\" align=\"right\"><strong class=\"p\">已婚 </strong></td></tr></tbody></table><br/><table width=\"980\" height=\"55\" border=\"1\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" bordercolor=\"#e5e1e1\"><tbody><tr bordercolor=\"#a7dfed\" bgcolor=\"#89cbe4\"><td width=\"100%\" height=\"35\" align=\"center\" bordercolor=\"#e5e1e1\" bgcolor=\"#9e9bd2\"><strong class=\"h1\" style=\"color: white\"> 信贷记录</strong></td></tr><tr bordercolor=\"#a7dfed\"><td height=\"18\" align=\"center\" bordercolor=\"#e5e1e1\" bgcolor=\"#f1edec\"><strong class=\"p\"> 这部分包含您的信用卡、贷款和其他信贷记录。金额类数据均以人民币计算，精确到元。</strong></td></tr></tbody></table><br/><table width=\"100%\"><tbody><tr><td width=\"782\"><p class=\"p\"><strong><span class=\"h1\">信息概要</span></strong></p><table width=\"100%\" align=\"center\"><tbody><tr><td width=\"100%\"></td></tr></tbody></table></td><td width=\"186\" rowspan=\"4\" valign=\"top\"><div style=\"background-color: #f1edec\" class=\"p\"><span style=\"background-color: #f1edec\">																逾期记录可能影响对您的信用评价。 </span></div><br/><br/><div class=\"p\" style=\"background-color: #f1edec\"><span style=\"background-color: #f1edec\">购房贷款，包括个人住房贷款、个人商用房（包括商住两用）贷款和个人住房公积金贷款。</span></div><br/><div class=\"p\" style=\"background-color: #f1edec\"><span>																发生过逾期的信用卡账户，指曾经“未按时还最低还款额”的贷记卡账户和曾经“连续透支60天以上”的准贷记卡账户。 </span></div></td></tr><tr><td width=\"782\"/></tr><tr><td width=\"782\"><table width=\"100%\" align=\"center\"><tbody><tr><td width=\"100%\"><table border=\"1\" cellspacing=\"0\" bordercolor=\"#e5e1e1\" cellpadding=\"0\" width=\"100%\" height=\"155\"><tbody><tr bordercolor=\"#89cbe4\"><td width=\"333\" align=\"center\" bordercolor=\"#e5e1e1\" class=\"p\">																						 																					</td><td width=\"300\" align=\"center\" bordercolor=\"#e5e1e1\" class=\"p\">																						信用卡																					</td><td width=\"200\" align=\"center\" bordercolor=\"#e5e1e1\" class=\"p\">																						购房贷款																					</td><td width=\"200\" align=\"center\" bordercolor=\"#e5e1e1\" class=\"p\">																						其他贷款																					</td></tr><tr><td align=\"left\" class=\"p\">																						 账户数																					</td><td align=\"center\" class=\"p\">																						13																					</td><td align=\"center\" class=\"p\">																						0																					</td><td align=\"center\" class=\"p\">																						15																					</td></tr><tr><td align=\"left\" class=\"p\">																						   未结清/未销户账户数																					</td><td align=\"center\" class=\"p\">																						5																					</td><td align=\"center\" class=\"p\">																						0																					</td><td align=\"center\" class=\"p\">																						12																					</td></tr><tr><td align=\"left\" class=\"p\">																						 发生过逾期的账户数																					</td><td align=\"center\" class=\"p\">																						1																					</td><td align=\"center\" class=\"p\">																						0																					</td><td align=\"center\" class=\"p\">																						0																					</td></tr><tr><td align=\"left\" class=\"p\">																						   发生过90天以上逾期的账户数																					</td><td align=\"center\" class=\"p\">																						0																					</td><td align=\"center\" class=\"p\">																						0																					</td><td align=\"center\" class=\"p\">																						0																					</td></tr><tr><td align=\"left\" class=\"p\">																						 为他人担保笔数																					</td><td align=\"center\" class=\"p\">																							0																						</td><td align=\"center\" class=\"p\">																							0																						</td><td align=\"center\" class=\"p\">																							0																						</td></tr></tbody></table></td></tr></tbody></table></td></tr><tr><td width=\"782\"></td></tr></tbody></table><span class=\"h1\"><strong>信用卡 </strong></span><ol class=\"p olstyle\"><span class=\"spanem\"><strong>发生过逾期的贷记卡账户明细如下：</strong></span><li style=\"list-style-type: decimal; list-style-position: outside\">														2007年6月13日招商银行发放的贷记卡（人民币账户）。截至2018年2月，信用额度22,000，已使用额度16,650。最近5年内有16个月处于逾期状态，没有发生过90天以上逾期。													</li><br/><span class=\"spanem\"><strong>从未逾期过的贷记卡及透支未超过60天的准贷记卡账户明细如下：</strong></span><li style=\"list-style-type: decimal; list-style-position: outside\">														2016年6月30日中国建设银行湖南省分行发放的贷记卡（人民币账户）。截至2018年2月，信用额度14,000，已使用额度14,201。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2007年6月16日兴业银行发放的贷记卡（美元账户）。截至2018年2月,信用额度折合人民币5,000，已使用额度0。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2007年6月16日兴业银行发放的贷记卡（人民币账户）。截至2018年2月，信用额度5,000，已使用额度0。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2007年6月13日招商银行发放的贷记卡（美元账户）。截至2018年2月,信用额度折合人民币22,000，已使用额度0。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2007年8月3日交通银行发放的贷记卡（人民币账户），截至2010年7月已销户。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2007年8月3日交通银行发放的贷记卡（美元账户），截至2010年7月已销户。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2007年6月12日平安银行信用卡中心发放的贷记卡（人民币账户），截至2011年3月已销户。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2007年6月12日平安银行信用卡中心发放的贷记卡（美元账户），截至2012年4月已销户。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2007年5月19日中信银行发放的贷记卡（美元账户），截至2009年7月已销户。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2007年5月19日中信银行发放的贷记卡（美元账户），截至2009年7月已销户。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2007年5月19日中信银行发放的贷记卡（人民币账户），截至2010年3月已销户。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2007年5月19日中信银行发放的贷记卡（人民币账户），截至2010年3月已销户。													</li></ol><span class=\"h1\"><strong>其他贷款</strong></span><ol class=\"p olstyle\"><span class=\"spanem\"><strong>从未逾期过的账户明细如下：<br/></strong></span><li style=\"list-style-type: decimal; list-style-position: outside\">														2017年8月28日捷信消费金融公司发放的5,098元（人民币）个人消费贷款，2019年8月31日到期。截至2018年1月，余额4,203。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2017年6月18日中银消费金融有限公司发放的10,000元（人民币）个人消费贷款，2018年6月30日到期。截至2018年1月，余额4,436。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2017年5月29日深圳前海微众银行股份有限公司发放的24,000元（人民币）个人消费贷款，2019年1月24日到期。截至2018年1月，余额14,400。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2017年4月25日马上消费金融股份有限公司发放的1,500元（人民币）个人消费贷款，2018年10月25日到期。截至2018年1月，余额10。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2017年4月16日中国对外经济贸易信托有限公司发放的5,100元（人民币）个人消费贷款，2018年4月16日到期。截至2018年2月，余额850。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2016年7月10日中银消费金融有限公司发放的5,000元（人民币）个人消费贷款，2018年5月31日到期。截至2018年1月，余额3,410。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2016年5月26日深圳平安普惠小额贷款有限公司发放的4,500元（人民币）个人消费贷款，2021年5月26日到期。截至2018年1月，余额4,500。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2018年1月18日南京银行股份有限公司发放的4,621元（人民币）其他贷款，2018年7月18日到期。截至2018年1月，余额4,621。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2017年12月20日南京银行股份有限公司发放的1,740元（人民币）其他贷款，2018年3月20日到期。截至2018年1月，余额1,164。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2017年11月21日南京银行股份有限公司发放的1,306元（人民币）其他贷款，2018年2月21日到期。截至2018年1月，余额438。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2017年9月7日南京银行股份有限公司发放的3,000元（人民币）其他贷款，2018年9月7日到期。截至2018年1月，余额2,023。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2017年6月23日南京银行股份有限公司发放的5,000元（人民币）其他贷款，2018年6月23日到期。截至2018年1月，余额2,120。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2017年9月26日渤海国际信托股份有限公司发放的1,250元（人民币）个人消费贷款，2017年10月已结清。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2017年10月23日南京银行股份有限公司发放的1,916元（人民币）其他贷款，2018年1月已结清。													</li><li style=\"list-style-type: decimal; list-style-position: outside\">														2017年8月21日南京银行股份有限公司发放的833元（人民币）其他贷款，2017年9月已结清。													</li></ol><br/><table width=\"980\" border=\"1\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" bordercolor=\"#e5e1e1\"><tbody><tr bordercolor=\"#bfdfff\" bgcolor=\"#a7dfed\"><td width=\"100%\" height=\"35\" align=\"center\" bordercolor=\"#e5e1e1\" bgcolor=\"#9e9bd2\" class=\"h1\" style=\"color: white\">											 公共记录										</td></tr><tr bordercolor=\"#a7dfed\" bgcolor=\"#bfdfff\"><td height=\"18\" align=\"center\" bordercolor=\"#e5e1e1\" bgcolor=\"#f1edec\"><strong class=\"p\"> 系统中没有您最近5年内的欠税记录、民事判决记录、强制执行记录、行政处罚记录及电信欠费记录。 </strong></td></tr></tbody></table><br/><br/><br/><table width=\"980\" border=\"1\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" bordercolor=\"#e5e1e1\"><tbody><tr bordercolor=\"#89cbe4\"><td width=\"100%\" height=\"35\" align=\"center\" bordercolor=\"#e5e1e1\" bgcolor=\"#9e9bd2\" class=\"h1\" style=\"color: white\">											查询记录										</td></tr><tr bordercolor=\"#a7dfed\" bgcolor=\"#bfdfff\"><td height=\"18\" align=\"center\" bordercolor=\"#e5e1e1\" bgcolor=\"#f1edec\"><strong class=\"p\">这部分包含您的信用报告最近2年被查询的记录。</strong></td></tr></tbody></table><table width=\"980\" border=\"0\" align=\"center\" cellspacing=\"0\" style=\"margin-top: 12px\"><tbody><tr><td align=\"center\" colspan=\"4\" valign=\"middle\"><strong class=\"p\">机构查询记录明细</strong></td></tr><tr><td colspan=\"4\" class=\"p\">------------------------------------------------------------------------------------------------------------------------------------------------------------------</td></tr><tr align=\"center\"><td width=\"100\" class=\"p\"><strong>编号</strong></td><td width=\"200\" class=\"p\"><strong>查询日期</strong></td><td width=\"380\" class=\"p\"><strong>查询操作员</strong></td><td width=\"300\" class=\"p\"><strong>查询原因</strong></td></tr><tr align=\"center\"><td class=\"p\">															1														</td><td class=\"p\">															2018年3月6日														</td><td class=\"p\">															广发银行/*9*9999999														</td><td class=\"p\">															信用卡审批														</td></tr><tr align=\"center\"><td class=\"p\">															2														</td><td class=\"p\">															2018年3月4日														</td><td class=\"p\">															中国建设银行湖南省分行/c*b														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															3														</td><td class=\"p\">															2018年3月3日														</td><td class=\"p\">															中国工商银行/19*1021*5														</td><td class=\"p\">															信用卡审批														</td></tr><tr align=\"center\"><td class=\"p\">															4														</td><td class=\"p\">															2018年2月16日														</td><td class=\"p\">															上海浦东发展银行/*10*2932														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															5														</td><td class=\"p\">															2018年2月10日														</td><td class=\"p\">															招商银行/*MBUS*R003														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															6														</td><td class=\"p\">															2018年2月6日														</td><td class=\"p\">															中国建设银行/A67*3*04														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															7														</td><td class=\"p\">															2018年1月7日														</td><td class=\"p\">															招商银行/*MBUSER*04														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															8														</td><td class=\"p\">															2017年11月18日														</td><td class=\"p\">															中银消费金融有限公司/*fc_*x01														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															9														</td><td class=\"p\">															2017年11月15日														</td><td class=\"p\">															平安银行信用卡中心/*PXT														</td><td class=\"p\">															信用卡审批														</td></tr><tr align=\"center\"><td class=\"p\">															10														</td><td class=\"p\">															2017年11月15日														</td><td class=\"p\">															中国民生银行/ms*h*x1														</td><td class=\"p\">															信用卡审批														</td></tr><tr align=\"center\"><td class=\"p\">															11														</td><td class=\"p\">															2017年11月1日														</td><td class=\"p\">															中国建设银行/zhang*an*.zh														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															12														</td><td class=\"p\">															2017年10月11日														</td><td class=\"p\">															深圳前海微众银行股份有限公司/w*zxcx_api_*2														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															13														</td><td class=\"p\">															2017年9月28日														</td><td class=\"p\">															中国对外经济贸易信托有限公司/*iao*eichaxun														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															14														</td><td class=\"p\">															2017年9月5日														</td><td class=\"p\">															中国建设银行/A*7*3104														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															15														</td><td class=\"p\">															2017年8月28日														</td><td class=\"p\">															捷信消费金融公司/enqui*y*7														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															16														</td><td class=\"p\">															2017年8月17日														</td><td class=\"p\">															马上消费金融股份有限公司/zido*g														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															17														</td><td class=\"p\">															2017年8月11日														</td><td class=\"p\">															中银消费金融有限公司/*fc*fx01														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															18														</td><td class=\"p\">															2017年8月7日														</td><td class=\"p\">															中国建设银行/A*7331*4														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															19														</td><td class=\"p\">															2017年7月20日														</td><td class=\"p\">															上海银行福民支行/fa*jy														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															20														</td><td class=\"p\">															2017年7月6日														</td><td class=\"p\">															招商银行/CM*U*ER003														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															21														</td><td class=\"p\">															2017年6月22日														</td><td class=\"p\">															南京银行股份有限公司/grb*6														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															22														</td><td class=\"p\">															2017年6月7日														</td><td class=\"p\">															众安在线财产保险股份有限公司/s*_za_c*axun01														</td><td class=\"p\">															保前审查														</td></tr><tr align=\"center\"><td class=\"p\">															23														</td><td class=\"p\">															2017年6月5日														</td><td class=\"p\">															招联消费金融有限公司/*l*uhuali														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															24														</td><td class=\"p\">															2017年5月29日														</td><td class=\"p\">															深圳前海微众银行股份有限公司/wzzx*x*api_02														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															25														</td><td class=\"p\">															2017年4月27日														</td><td class=\"p\">															南京银行股份有限公司/g*b26														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															26														</td><td class=\"p\">															2017年4月25日														</td><td class=\"p\">															马上消费金融股份有限公司/*idong														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															27														</td><td class=\"p\">															2017年4月13日														</td><td class=\"p\">															中国对外经济贸易信托有限公司/x*aowei*haxun														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															28														</td><td class=\"p\">															2017年4月1日														</td><td class=\"p\">															中国建设银行湖南省长沙市铁银支行/992*1*06														</td><td class=\"p\">															信用卡审批														</td></tr><tr align=\"center\"><td class=\"p\">															29														</td><td class=\"p\">															2017年3月23日														</td><td class=\"p\">															中银消费金融有限公司/*fc_f*01														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															30														</td><td class=\"p\">															2017年3月15日														</td><td class=\"p\">															平安银行信用卡中心/*PXT														</td><td class=\"p\">															信用卡审批														</td></tr><tr align=\"center\"><td class=\"p\">															31														</td><td class=\"p\">															2017年3月12日														</td><td class=\"p\">															招商银行/CMBU*ER*04														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															32														</td><td class=\"p\">															2016年12月9日														</td><td class=\"p\">															中国对外经济贸易信托有限公司/*iaoweic*axun														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															33														</td><td class=\"p\">															2016年12月4日														</td><td class=\"p\">															招商银行/CM*U*ER004														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															34														</td><td class=\"p\">															2016年12月2日														</td><td class=\"p\">															中银消费金融有限公司/cfc*fx*1														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															35														</td><td class=\"p\">															2016年8月9日														</td><td class=\"p\">															中银消费金融有限公司/*fc_fx*1														</td><td class=\"p\">															贷后管理														</td></tr><tr align=\"center\"><td class=\"p\">															36														</td><td class=\"p\">															2016年7月28日														</td><td class=\"p\">															北京拉卡拉小额贷款有限责任公司/0197*1														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															37														</td><td class=\"p\">															2016年7月25日														</td><td class=\"p\">															广发银行/999*99*999														</td><td class=\"p\">															信用卡审批														</td></tr><tr align=\"center\"><td class=\"p\">															38														</td><td class=\"p\">															2016年7月19日														</td><td class=\"p\">															中国平安财产保险股份有限公司/*inbao														</td><td class=\"p\">															保前审查														</td></tr><tr align=\"center\"><td class=\"p\">															39														</td><td class=\"p\">															2016年7月10日														</td><td class=\"p\">															中银消费金融有限公司/cf*_*x01														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															40														</td><td class=\"p\">															2016年6月30日														</td><td class=\"p\">															中国建设银行湖南省长沙市铁银支行/9923*3*6														</td><td class=\"p\">															信用卡审批														</td></tr><tr align=\"center\"><td class=\"p\">															41														</td><td class=\"p\">															2016年5月26日														</td><td class=\"p\">															平安银行信用卡中心/SP*T														</td><td class=\"p\">															信用卡审批														</td></tr><tr align=\"center\"><td class=\"p\">															42														</td><td class=\"p\">															2016年5月26日														</td><td class=\"p\">															深圳平安普惠小额贷款有限公司/szxa*d*x														</td><td class=\"p\">															贷款审批														</td></tr><tr align=\"center\"><td class=\"p\">															43														</td><td class=\"p\">															2016年5月17日														</td><td class=\"p\">															招商银行/*MBUSER*03														</td><td class=\"p\">															贷后管理														</td></tr><tr><td colspan=\"4\" class=\"p\">------------------------------------------------------------------------------------------------------------------------------------------------------------------</td></tr></tbody></table><table width=\"980\" border=\"0\" align=\"center\" cellspacing=\"0\"><tbody><tr><td height=\"18\" align=\"center\" bordercolor=\"#e5e1e1\" colspan=\"4\"><strong class=\"p\">个人查询记录明细</strong></td></tr><tr><td colspan=\"4\" class=\"p\">------------------------------------------------------------------------------------------------------------------------------------------------------------------</td></tr><tr align=\"center\"><td width=\"100\" class=\"p\"><strong>编号</strong></td><td width=\"200\" class=\"p\"><strong>查询日期</strong></td><td width=\"380\" class=\"p\"><strong>查询操作员</strong></td><td width=\"300\" class=\"p\"><strong>查询原因</strong></td></tr><tr align=\"center\"><td class=\"p\">															1														</td><td class=\"p\">															2017年11月21日														</td><td class=\"p\">															本人														</td><td class=\"p\">															本人查询（互联网个人信用信息服务平台）														</td></tr><tr align=\"center\"><td class=\"p\">															2														</td><td class=\"p\">															2017年9月19日														</td><td class=\"p\">															本人														</td><td class=\"p\">															本人查询（互联网个人信用信息服务平台）														</td></tr><tr align=\"center\"><td class=\"p\">															3														</td><td class=\"p\">															2017年9月6日														</td><td class=\"p\">															本人														</td><td class=\"p\">															本人查询（互联网个人信用信息服务平台）														</td></tr><tr align=\"center\"><td class=\"p\">															4														</td><td class=\"p\">															2017年7月26日														</td><td class=\"p\">															本人														</td><td class=\"p\">															本人查询（互联网个人信用信息服务平台）														</td></tr><tr align=\"center\"><td class=\"p\">															5														</td><td class=\"p\">															2017年7月15日														</td><td class=\"p\">															本人														</td><td class=\"p\">															本人查询（互联网个人信用信息服务平台）														</td></tr><tr align=\"center\"><td class=\"p\">															6														</td><td class=\"p\">															2017年4月12日														</td><td class=\"p\">															本人														</td><td class=\"p\">															本人查询（互联网个人信用信息服务平台）														</td></tr><tr align=\"center\"><td class=\"p\">															7														</td><td class=\"p\">															2017年3月11日														</td><td class=\"p\">															本人														</td><td class=\"p\">															本人查询（互联网个人信用信息服务平台）														</td></tr><tr align=\"center\"><td class=\"p\">															8														</td><td class=\"p\">															2016年7月8日														</td><td class=\"p\">															中国人民银行长沙中心支行/PB*cs_zhzzc*1														</td><td class=\"p\">															本人查询（临柜）														</td></tr><tr align=\"center\"><td class=\"p\">															9														</td><td class=\"p\">															2016年6月30日														</td><td class=\"p\">															中国人民银行长沙中心支行/PB*cs_zhzzc*1														</td><td class=\"p\">															本人查询（临柜）														</td></tr><tr align=\"center\"><td class=\"p\">															10														</td><td class=\"p\">															2016年6月27日														</td><td class=\"p\">															本人														</td><td class=\"p\">															本人查询（互联网个人信用信息服务平台）														</td></tr><tr align=\"center\"><td class=\"p\">															11														</td><td class=\"p\">															2016年6月17日														</td><td class=\"p\">															中国人民银行长沙中心支行/PBCcs_zhz*c*2														</td><td class=\"p\">															本人查询（临柜）														</td></tr><tr><td colspan=\"4\" class=\"p\">------------------------------------------------------------------------------------------------------------------------------------------------------------------</td></tr></tbody></table><br/><table width=\"980\" border=\"0\" align=\"center\" cellspacing=\"0\"><tbody><tr><td width=\"980\" align=\"center\"><strong class=\"h1\">说  明</strong></td></tr></tbody></table><ol><li class=\"p\" style=\"list-style-type: decimal; list-style-position: outside\">									除查询记录外，本报告中的信息是依据截至报告时间个人征信系统记录的信息生成，征信中心不确保其真实性和准确性，但承诺在信息汇总、加工、整合的全过程中保持客观、中立的地位。								</li><li class=\"p\" style=\"list-style-type: decimal; list-style-position: outside\">									本报告仅包含可能影响您信用评价的主要信息，如需获取您在个人征信系统中更详细的记录，请到当地信用报告查询网点查询。信用报告查询网点的具体地址及联系方式可访问征信中心门户网站（www.pbccrc.org.cn）查询。								</li><li class=\"p\" style=\"list-style-type: decimal; list-style-position: outside\">									您有权对本报告中的内容提出异议。如有异议，可联系数据提供单位，也可到当地信用报告查询网点提出异议申请。								</li><li class=\"p\" style=\"list-style-type: decimal; list-style-position: outside\">									本报告仅供您了解自己的信用状况，请妥善保管。因保管不当造成个人隐私泄露的，征信中心将不承担相关责任。								</li><li class=\"p\" style=\"list-style-type: decimal; list-style-position: outside\">									更多咨询，请致电全国客户服务热线400-810-8866。								</li></ol><div align=\"center\"><a href=\"https://ipcrs.pbccrc.org.cn/reportAction.do?method=downloadwelcome\" style=\"text-decoration: none\"><img height=\"20\" border=\"0\" id=\"downloadpdf\" width=\"55\" src=\"https://ipcrs.pbccrc.org.cn/images/button_pdf.jpg\"/></a><span style=\"font-size: 13px; vertical-align:text-top;\">(点此按钮可将信用报告保存至本地或打印)</span>									        								<a href=\"javascript:window.close()\"><img height=\"20\" border=\"0\" width=\"55\" src=\"https://ipcrs.pbccrc.org.cn/images/button_gb.jpg\"/></a></div><br/><br/><br/><br/></td></tr><tr><td>						 					</td></tr></tbody></table></div></div><script language=\"JavaScript\">//<![CDATA[ self.moveTo(0,0) self.resizeTo(screen.availWidth,screen.availHeight) //]]></script></body></html>", "1346579"));
		}*/
}
