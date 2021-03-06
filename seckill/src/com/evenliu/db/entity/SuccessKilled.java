package com.evenliu.db.entity;

import java.util.Date;

public class SuccessKilled extends SuccessKilledKey {
  
	/**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column success_killed.state
     *
     * @mbggenerated Mon Aug 22 10:06:00 CST 2016
     */
    private Byte state;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column success_killed.create_time
     *
     * @mbggenerated Mon Aug 22 10:06:00 CST 2016
     */
    private Date createTime;
    
    //多对一
    private Seckill seckill;
    
    public SuccessKilled() {
  		super();
  	}
    public SuccessKilled(Long seckillId, Long userPhone) {
  		super(seckillId, userPhone);
  		// TODO Auto-generated constructor stub
  	}

    public Seckill getSeckill() {
		return seckill;
	}

	public void setSeckill(Seckill seckill) {
		this.seckill = seckill;
	}

	/**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column success_killed.state
     *
     * @return the value of success_killed.state
     *
     * @mbggenerated Mon Aug 22 10:06:00 CST 2016
     */
    public Byte getState() {
        return state;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column success_killed.state
     *
     * @param state the value for success_killed.state
     *
     * @mbggenerated Mon Aug 22 10:06:00 CST 2016
     */
    public void setState(Byte state) {
        this.state = state;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column success_killed.create_time
     *
     * @return the value of success_killed.create_time
     *
     * @mbggenerated Mon Aug 22 10:06:00 CST 2016
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column success_killed.create_time
     *
     * @param createTime the value for success_killed.create_time
     *
     * @mbggenerated Mon Aug 22 10:06:00 CST 2016
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	@Override
	public String toString() {
		return "SuccessKilled [state=" + state + ", createTime=" + createTime
				+ ", seckill=" + seckill + "]";
	}
    
    
}