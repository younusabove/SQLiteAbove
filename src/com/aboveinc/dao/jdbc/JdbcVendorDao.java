package com.aboveinc.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aboveinc.provider.VendorEntity;
@Repository
public class JdbcVendorDao {

	private static final String VENDOR_QUERY="select  yearly.v_id,yearly.vname,nullif(monthly.monthlysum,'0'),nullif(Quertly.quaterlysum,'0'),yearly.yearlysum  from "+ 
	 " (select vp.vendor_id  as v_id,v.vendorname as vname ,sum(po.purchaseamount) as yearlysum from purchase_order po  join   "+
	 "     		 vendor_purchase vp  on vp.purchase_id=po.purchase_id   join vendor v on v.vendor_id=vp.vendor_id  "+
	 "    	   where purchasedate>date('now') and  purchasedate<date('now','+365 day') group by vp.vendor_id,v.vendorname) yearly "+
	 "      left  join  (select vp.vendor_id  as v_id,v.vendorname,sum(po.purchaseamount) as quaterlysum from purchase_order po  join "+ 
	 "    	 vendor_purchase vp  on vp.purchase_id=po.purchase_id   join vendor v on v.vendor_id=vp.vendor_id  "+
	 "    	   where purchasedate>date('now')  and purchasedate<date('now','+90 day') group by vp.vendor_id,v.vendorname) Quertly "+ 
	 "        on  Quertly.v_id=yearly.v_id     left   join  "+
	 "       (select vp.vendor_id  as v_id,v.vendorname,sum(po.purchaseamount) as monthlysum from purchase_order po  join "+ 
	 "    	 vendor_purchase vp  on vp.purchase_id=po.purchase_id   join vendor v on v.vendor_id=vp.vendor_id "+
	 "    	   where purchasedate>date('now')  and purchasedate<date('now','+30 day') group by vp.vendor_id,v.vendorname) monthly "+ 
	 "      on monthly.v_id=yearly.v_id       union "+
	 "      select v.vendor_id,v.vendorname,'0' as monthly,'0' as Quaterly,'0' as Yearly from vendor v "+
	 "      where vendor_id not in (select vendor_id from vendor_purchase )" ;
	
	private static final String VENDOR_ID="select seq+1 from sqlite_sequence where name='vendor'";
	private static final String VENDOR_INSERT="insert into vendor(vendor_id,vendorname) values(?,?)";
	private static final String VENDOR_UPDATE="update vendor set vendorname=? where vendor_id=?";
	private static final String[] VENDOR_PURCHASE_DELETE={"delete from purchase_order where purchase_id in (select purchase_id from vendor_purchase where vendor_id=?)","delete from vendor_purchase where vendor_id=?","delete from vendor where vendor_id=?"};
	@Autowired
	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public JdbcVendorDao(){
	}

	
	public List<VendorEntity> getVendor(){
		@SuppressWarnings("unchecked")
		List<VendorEntity> vendors= jdbcTemplate.query(VENDOR_QUERY, new RowMapper(){
			public Object mapRow(ResultSet rs ,int rowNum)throws SQLException,DataAccessException{
				VendorEntity vendorEntity= new VendorEntity();
				vendorEntity.setId(rs.getString(1));
				vendorEntity.setVendorName(rs.getString(2));
				vendorEntity.setMonthly(rs.getString(3));
				vendorEntity.setQuaterly(rs.getString(4));
				vendorEntity.setYearly(rs.getString(5));
				return vendorEntity;
				
			}
		});
		return vendors.size()>0? vendors:null;
	}
	
	public String  addVendor(String vendorName){
		Long vendorId=jdbcTemplate.queryForLong(VENDOR_ID);
		jdbcTemplate.update(VENDOR_INSERT,new Object[]{vendorId,vendorName});
		
		return String.valueOf(vendorId);
	}
	
	public Long  getVendorId(){
			return jdbcTemplate.queryForLong(VENDOR_ID);
	}
	public void  updateVendor(VendorEntity vendorEntity){
		
		jdbcTemplate.update(VENDOR_UPDATE,new Object[]{vendorEntity.getVendorName(),vendorEntity.getId()});
		
		
	
	}
	
	public void  removeVendor(String  vendorId){

		String[] modifiedQuery = new String[3];
		
		for( int i = 0; i < VENDOR_PURCHASE_DELETE.length; i++)
		{
		    String query = VENDOR_PURCHASE_DELETE[i];
		   String delQuery=  query.replace('?', vendorId.charAt(0));
		    modifiedQuery[i]=delQuery;
		    }
		
		jdbcTemplate.batchUpdate(modifiedQuery);
		
	
	}
	
	
}
