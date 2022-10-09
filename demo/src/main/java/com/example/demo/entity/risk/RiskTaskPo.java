package com.example.demo.entity.risk;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "t_risk_task")
@Entity
@Data
public class RiskTaskPo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//执行id

    @Column(name = "tenant_id")
    private Long tenantId;//商户id

    @Column(name = "task_no")
    private String taskNo;//task编号

    @Column(name = "flow_id")
    private Long flowId;//流程id

    @Column(name = "out_order_id")
    private String outOrderId;//外部订单id

    @Column(name = "out_user_id")
    private String outUserId;//外部用户id

    @Column(name = "order_id")
    private Long orderId;//订单

    @Column(name = "user_id")
    private Long userId;//用户id

    @Column(name = "balance")
    private Integer balance;//借款金额

    @Column(name = "app_id")
    private Long appId;//app_id

    @Column(name = "biz_id")
    private String bizId;//业务id

    @Column(name = "classify_rule_id")
    private Long classifyRuleId;//客群分组id，对应规则id

    @Column(name = "rule_group_id")
    private Long ruleGroupId;//规则组id

    @Column(name = "hit_result")
    private String hitResult;//命中规则结果

    @Column(name = "score")
    private Integer score;//流程分数

    @Column(name = "hit_score_result")
    private String hitScoreResult;//命中分数结果

    @Column(name = "model_score")
    private BigDecimal modelScore;//模型分

    @Column(name = "is_blacklist")
    private Integer isBlacklist;//是否命中外部黑名单

    @Column(name = "final_decision")
    private Integer finalDecision;//最终结果

    @Column(name = "decision_type")
    private Integer decisionType;//决策类型

    @Column(name = "decision_result")
    private Integer decisionResult;//决策结果

    private Integer status;//节点状态

    @Column(name = "is_test")
    private Integer isTest;//是否线上测试: 0,正常流程 1.线上测试流程

    @Column(name = "user_name")
    private String userName;//姓名

    @Column(name = "mobile_phone")
    private String mobilePhone;//手机号

    @Column(name = "identity_no")
    private String identityNo;//身份证号

    @CreationTimestamp
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
