package com.qqz.hedgecal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * 本计算器用于根据不同博彩公司赔率给出对冲投资建议
 * @author 邱启哲
 *
 */
public class Calculator {

	private static int platformNum;
	private static int resultNum;
	private static Scanner scanner = new Scanner(System.in);
	public static void main(String[] args) {
		printHead();
		getPlatformInfo();
		List<BigDecimal[]> odds = getOdds();
		provideAdvice(odds);
		printTail();
		
	}
	
	/**
	 * 根据各平台赔率信息，分析并给出结果
	 * @param odds 各平台对某场比赛开出的赔率
	 */
	private static void provideAdvice(List<BigDecimal[]> odds) {
		BigDecimal[] highestodds = new BigDecimal[resultNum];	//存各个结果的所有平台最高赔率
		List<Integer>[] purchaseStrategy = new ArrayList[platformNum];	//存各个平台上要买的结果编号
				
		for(int i = 0 ; i < resultNum ; i++) {	//遍历所有结果
			BigDecimal highestodd = BigDecimal.ZERO;
			int platform_id = -1;
			for(int j = 0 ; j < platformNum ; j++) {	//遍历所有平台，找到某一个比赛结果中赔率最高的平台
				BigDecimal[] odd = odds.get(j);	//获取某一平台的赔率
				if(odd[i].compareTo(highestodd) >= 0) {
					highestodd = odd[i];
					platform_id = j;
				}				
			}			
			highestodds[i] = highestodd;
			if(purchaseStrategy[platform_id] == null)	purchaseStrategy[platform_id] = new ArrayList();
			purchaseStrategy[platform_id].add(i);
		}
		
		BigDecimal marker = new BigDecimal("100");	//用户此时没有输入投资金额信息，先用100判断是不是能稳赚.此处的100表示希望收回100
		BigDecimal spent = BigDecimal.ZERO;
		for(BigDecimal odd : highestodds) {
			spent = spent.add(marker.divide(odd, 6, RoundingMode.HALF_DOWN));	//spent = spent + 100/赔率
		}
		if(marker.compareTo(spent) <= 0) {
			System.out.println("本场比赛中各平台的赔率无法进行对冲操作，祝下次好运！");
		}else {
			System.out.println("可以通过对冲操作获利，接下来您可输入不同金额进行计算(输入‘#’退出)");
			while(true) {
				System.out.println("请输入要回收的金额：");
				try {
					String userInput = scanner.next();
					if(userInput.equals("#"))	return;	//处理用户退出指令
					System.out.println("------------------开始分析，请稍候------------------");
					BigDecimal moneyBack = new BigDecimal(userInput);
					BigDecimal moneySpent = BigDecimal.ZERO;
					for(BigDecimal odd : highestodds) {
						moneySpent = moneySpent.add(moneyBack.divide(odd, 6, RoundingMode.HALF_DOWN));	//gain = spent + 100/赔率
					}
					System.out.println("具体投资配额如下:");
					for(int i = 0 ; i < purchaseStrategy.length ; i++) {
						List<Integer> list = purchaseStrategy[i];
						System.out.println("第 " + (i + 1) + " 家平台投注的结果：");
						for(int j = 0 ; j < list.size() ; j++) {
							System.out.println("    -第 " + (list.get(j) + 1) + " 号结果投注" + moneyBack.divide(highestodds[list.get(j)], 6, RoundingMode.HALF_DOWN) + "元");
						}
						System.out.println("");
					}
					System.out.println("总投资 " + moneySpent + " 元，预计可收回 " + moneyBack + " 元，预计收益 " + (moneyBack.subtract(moneySpent)) + " 元");
					System.out.println("------------------结果分割线------------------");
				} catch (NumberFormatException e) {
					System.out.println("数据格式错误，请重启程序再来一次。");
					System.exit(0);
				}
			}
			
		}
	}
	
	/**
	 * 获得平台数量和比赛结果数量的信息
	 */
	private static void getPlatformInfo() {
						
		try {
			System.out.println("请输入希望进行对冲操作的平台数量：");
			platformNum = scanner.nextInt();
			System.out.println("请输入比赛结果数量：");
			resultNum = scanner.nextInt();
			if(platformNum <= 1 || resultNum <= 1) {
				System.out.println("输入的数据值有误，无法进行对冲计算，请重启程序再来一次");
				System.exit(0);
			}
		} catch (InputMismatchException e) {
			System.out.println("数据格式错误，请重启程序再来一次。");
			System.exit(0);
		}
	}
	
	/**
	 * 获得各平台赔率，每个平台赔率是list的一个元素
	 * @return 各平台赔率值
	 */
	private static List<BigDecimal[]> getOdds() {
		List<BigDecimal[]> odds = new ArrayList<>();
		for(int i = 1 ; i <= platformNum ; i++) {
			System.out.println("请输入 " + i + " 号平台的赔率(如1赔1.45则输入1.45)：");
			BigDecimal[] odd = new BigDecimal[resultNum];
			for(int j = 0 ; j < resultNum ; j++) {
				try {
					BigDecimal userInputOdd = new BigDecimal(scanner.next());
					if(userInputOdd.compareTo(BigDecimal.ONE) <= 0) {
						System.out.println("输入的数据值有误，无法进行对冲计算，请重启程序再来一次");
						System.exit(0);
					}
					odd[j] = userInputOdd;
				} catch (NumberFormatException e) {
					System.out.println("数据格式错误，请重启程序再来一次。");
					System.exit(0);
				}				
			}
			odds.add(odd);
		}
		
		return odds;
	}
		
	/**
	 * 打印程序欢迎页面
	 */
	private static void printHead() {
		System.out.println("*******************************对冲投资计算器*******************************");
		System.out.println("                          $$$$$$$$说明 $$$$$$$$"); 
		System.out.println("  1. 本计算器用于根据不同博彩公司对某场比赛的不同赔率给出对冲投资建议按照程序建议投资可以保证收益");
		System.out.println("  2. 使用方法:输入希望进行对冲操作的平台数量->输入结果数->输入各公司不同结果的赔率(注意顺序一致)");
		System.out.println("  3. 虽然本程序给出的投资建议可以保证稳赚不赔，但请始终牢记：投资有风险，入市需谨慎！");
		System.out.println("***********************************************************************");
	}
	
	/**
	 * 打印程序结束页面
	 */
	private static void printTail() {
		System.out.println("****************************谢谢使用！祝投资顺利！****************************");
		
	}
}
