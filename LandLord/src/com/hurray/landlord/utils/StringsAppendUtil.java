package com.hurray.landlord.utils;

import com.hurray.landlord.entity.UserInfo;

public class StringsAppendUtil {
	
	// 个人信息数组的拼接 依赖于配置文件
	public static String[] append(String[] mInformations, UserInfo mUserInfo){
		
		for (int i = 0; i < mInformations.length && null != mUserInfo; i++) {
			switch (i) {
			case 0:// 等级
				mInformations[i] += mUserInfo.getLevel().toString();
				break;
			case 1:// 性别
				switch (mUserInfo.getGender()) {
				case MALE:
					mInformations[i] += "男";
					break;
				case FEMALE:
					mInformations[i] += "女";
					break;
				}
				break;
			case 2:// 战绩
				mInformations[i] = new StringBuffer(mInformations[i])
					.append(mUserInfo.getVictoryNumber().toString())
					.append("胜")
					.append(mUserInfo.getFailureNumber().toString())
					.append("负").toString();
				break;
			case 3:// 金币
				mInformations[i] += mUserInfo.getGold().toString();
				break;
			case 4:// 元宝
				mInformations[i] += mUserInfo.getMoney().toString();
				break;
			case 5:// 积分
				mInformations[i] += mUserInfo.getScore().toString();
				break;
			case 6:// 状态
				switch (mUserInfo.getStatus()) {
				case FIGHTING:
					mInformations[i] += "战斗";
					break;
				case IDLE:
					mInformations[i] += "空闲";
					break;
				case WAITING:
					mInformations[i] += "等待";
					break;
				}
				break;
			case 7:// 头衔
				mInformations[i] += mUserInfo.getRank();
				break;
			/*case 8:// 成就
				mInformations[i] = new StringBuffer(mInformations[i])
					.append(mUserInfo.getAchievements().length)
					.append("/")
					.append(mUserInfo.getTotalAchievement()).toString();
				break;
			case 9:// 经验
				mInformations[i] = new StringBuffer(mInformations[i])
					.append(mUserInfo.getExperience().toString())
					.append("/")
					.append(mUserInfo.getNextExperience().toString()).toString();
				break;*/
			default:
				break;
			}
		}
		return mInformations;
	}	
}
