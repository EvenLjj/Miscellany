package com.evenliu.db.entity;

public class SuccessKilledKey {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column success_killed.seckill_id
     *
     * @mbggenerated Mon Aug 22 10:06:00 CST 2016
     */
    private Long seckillId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column success_killed.user_phone
     *
     * @mbggenerated Mon Aug 22 10:06:00 CST 2016
     */
    private Long userPhone;
    
    public SuccessKilledKey() {
		super();
	}
    
    /**
     *构造函数  
     */
    public SuccessKilledKey(Long seckillId, Long userPhone) {
		super();
		this.seckillId = seckillId;
		this.userPhone = userPhone;
	}
    
    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column success_killed.seckill_id
     *
     * @return the value of success_killed.seckill_id
     *
     * @mbggenerated Mon Aug 22 10:06:00 CST 2016
     */
    public Long getSeckillId() {
        return seckillId;
    }

	/**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column success_killed.seckill_id
     *
     * @param seckillId the value for success_killed.seckill_id
     *
     * @mbggenerated Mon Aug 22 10:06:00 CST 2016
     */
    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column success_killed.user_phone
     *
     * @return the value of success_killed.user_phone
     *
     * @mbggenerated Mon Aug 22 10:06:00 CST 2016
     */
    public Long getUserPhone() {
        return userPhone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column success_killed.user_phone
     *
     * @param userPhone the value for success_killed.user_phone
     *
     * @mbggenerated Mon Aug 22 10:06:00 CST 2016
     */
    public void setUserPhone(Long userPhone) {
        this.userPhone = userPhone;
    }
}