package com.sl.ylyy.manager.dao;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.sl.ylyy.manager.entity.Tip_Room_Relation;


@Mapper
public interface Tip_Room_RelationDao {
	
	public void updateTip_Room_Relation(Tip_Room_Relation Tip_Room_Relation);
	
	public void insertTip_Room_Relation(Tip_Room_Relation Tip_Room_Relation);
	
	public void deleteTip_Room_Relation(Tip_Room_Relation Tip_Room_Relation);
	
	public void deleteTip_Room_RelationByRoleId(Integer rid);
	
	public List getSwitchingroombyTip(int tid);

}
