  

select * from as_tab_col t where t.tab_id='SF_CERTIFICATE' for update;

select * from as_lang_trans l where l.res_id like 'SF_RECEIPT%' FOR UPDATE;
--场地管理 鉴定结果记录文件 SF_MOBILE_MSG 供应商管理 SF_SJ_PRODUCTOR 生产商管理
select * from as_table t where t.tab_id like 'SF_MATERIALS' for update;

select * from as_compo c where c.compo_id LIKE 'SF_RECEIPT%' for update;
--SF_JD_RECORD_FILE_MODEL
--ZC_EB_BULLETIN_XUN_JIA_BID
SELECT * FROM AS_COMPO_FUNC CF where cf.compo_id LIKE 'SF_ENTRUST1'  FOR UPDATE;

select * from as_func F where F.Func_Id like '%print%Tm' for update;
--fdisable fenable
SELECT * FROM ZC_SEARCH_CONDITION S where s.compo_id='SF_ENTRUST' FOR UPDATE;

select * from zc_user_search_condition s where s.condition_id like 'ZcEbQb_Tab' for update;

select * from zc_role_search_condition s where s.condition_id like 'ZcMobileMsg_Tab' for update;
--gys_normal gys_huiyuan
select * from ZC_SYS_BILL_ELEMENT e where e.bill_type_code like 'SF_RECEIPT' FOR UPDATE;

select * from AS_NO_RULE    where COMPO_ID = 'ZC_EB_QB' for update;
--DD_CODE ZC_DINGDIAN_GEN
SELECT * FROM AS_NUM_TOOL T WHERE T.NUM_TOOL_ID like 'ZC_EB_QB_GEN%' for update;
--SF_OUT_INFO_REQ_CODE_GEN_ID
--ZC_EB_PROJ_GEN
SELECT * FROM AS_NUM_TOOL_NO N WHERE N.NUM_TOOL_ID like 'ZC_EB_QB_GEN%' for update;

select * from AS_NO_RULE_SEG r where r.compo_id='ZC_EB_QB' for update;

select * from as_val v where v.valset_id like 'SF_VS_RECEIPT_STATUS' for update;

select * from as_val v where v.val like '启用';
select *　from as_valset v where v.valset_id='SF_VS_MAJOR' for update;

select * from as_option O WHERE O.OPT_ID LIKE 'OPT_SF_WTF_ROLE_ID1' for update;

select * from as_menu m where m.menu_id like 'ZC%' for update;
--SF
SELECT * FROM AS_MENU_COMPO MC WHERE MC.COMPO_ID LIKE 'SF_JD_RECORD_FILE_MODEL1%' FOR UPDATE;
--ZC_HUIYUAN
SELECT * FROM AP_MENU_COMPO MC WHERE MC.COMPO_ID LIKE 'SF_MOBILE_MSG1%' FOR UPDATE;

select * from as_file f where f.file_id='sf_entrust_template' for update;

select * from v_sf_user_func v where v.user_id='zhengjie' and v.FUNC_ID!='fwatch';

select * from as_role_num_lim r where r.compo_id='SF_ENTRUST' for update;

select * from as_role_num_lim r where r.Func_Id='fprintXy' for update;
--SQL_CONDITION

select * from as_role_func r where r.role_id='R_SF_WTF' and r.compo_id='SF_ENTRUST' for update;

select * from as_role_func r where r.Func_Id='SF_JD_PERSON' for update;

//查工作流模板
SELECT * FROM wf_template t WHERE t.template_id=?;
//查工作流节点 包括节点上的监听器
SELECT * FROM wf_node t WHERE t.template_id='31008';
//查节点对应的业务数据状态
SELECT * FROM wf_node_state t where t.Node_Id=?
//查工作流连接和上面的条件
SELECT * FROM wf_link t WHERE t.template_id='31008';  
//查工作流连接后续的业务数据状态，如最后一个节点，其连接路径的的连接线，会设置业务的数据状态，一般是exec
SELECT * FROM wf_link_state t WHERE t.Node_Link_Id=?;
//查找工作流上的状态集所对应的业务表和业务字段
SELECT * FROM as_wf_bind_state t WHERE t.wf_template_id='31008'; 
//查工作流模板上的变量,WF_VARIABLE是变量表,AS_WF_BIND_VARIABLE是变量相关的表达式
select b.variable_id       VARIABLE_ID,
       b.DESCRIPTION,
       b.TEMPLATE_ID,
       b.TYPE,
       BIND_EXPRESSION,
       TAB_ID,
       CONDITION,
       FILTER_BY_ENTITYKEY,
       b.name              NAME
  from AS_WF_BIND_VARIABLE a, WF_VARIABLE b
 where a.wf_variable = b.variable_id
   and WF_TEMPLATE_ID = '31008';
 

--AS_TAB_COL 插入sql
insert into as_tab_col 
 select A.table_name tab_id,
       A.column_name data_item,
       0 ord_index,
       NVL(a.comments,B.COLUMN_NAME) data_item_desc,
       NVL(a.comments,B.COLUMN_NAME) data_item_na,
       B.DATA_TYPE  data_type,
       B.DATA_LENGTH data_len,
       '' dec_len,
       '' f_ref_name,
       '' f_field,
       'Y' is_save,
       '' val_set_id,
       '' is_fpk,
       'Y' is_used,
       '' is_pre,
       '' is_pk,
       B.NULLABLE AS is_null,
       --'' is_null,
       '' is_num,
       '' is_list,
       '' is_sele,
       '' dflt_val,
       '' add_date,
       '' db_ver_no,
       '' is_page_field,
       '' min_value,
       '' max_value,
       '' min_length,
       '' is_effect,
       '' vs_effect_table,
       '' url,
       '' is_kilo_style,
       '' is_treeview,
       '' is_onlyleaf,
       '' edit_box_type,
       '' is_order
FROM user_col_comments a
    ,all_tab_columns b
WHERE a.table_name = b.table_name and
      a.table_name = 'SF_CERTIFICATE'
      AND B.OWNER='PUER'
      AND A.column_name=B.COLUMN_NAME;

