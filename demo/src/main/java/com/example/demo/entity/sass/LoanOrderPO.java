package com.example.demo.entity.sass;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "loan_order")
@Entity
@Data
public class LoanOrderPO implements Serializable {

    private static final long serialVersionUID = 7506645871089160291L;

    @Id
    private String id;
    private String user_id;
    private Integer amt;
    private Integer apply_fee;
    private Integer apply_fee_rate;
    private Integer period_days;
    private Integer defer_rate;
    //    private Integer overdue_fine_rate;
    private Integer rate;
    private String withdraw_order_id;
    private Date maturity;
    private Date credit_time;
    private Date first_audit_time;
    private Date last_audit_time;
    private Integer status;
    private String first_auditor_id;
    private String last_auditor_id;
    private String contract_id;
    private String loaner_id;
    private String repay_mark;
    private Boolean is_repeat;
    private Boolean is_pending;
    private String platform;
    private String installation_source;
    private Integer logic_score;
    private String company_id;
    private String app_id;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
    private String loan_purpose;

    private Integer postpone_apply_fee;
}