

select * from as_tab_col t where t.tab_id='ZC_DINGDIAN' for update;

select * from as_lang_trans l where l.res_id like '%ZC_DINGDIAN_ITEM_ITEM_DETAIL' FOR UPDATE;
--场地管理
select * from as_table t where t.tab_id like 'ZC_EB_BULLETIN' for update;

select * from as_compo c where c.compo_id LIKE 'ZC_DINGDIAN' for update;
--SF_EVALUATION fjixuyijiao
--ZC_EB_BULLETIN_XUN_JIA_BID
SELECT * FROM AS_COMPO_FUNC CF where cf.compo_id LIKE 'ZC_DINGDIAN'  FOR UPDATE;

select * from as_func F where F.Func_Id='fjieZhuanBaseData' for update;
--fdisable fenable
SELECT * FROM ZC_SEARCH_CONDITION S where s.compo_id='ZC_DINGDIAN' FOR UPDATE;

select * from zc_user_search_condition s where s.condition_id like 'ZcEbQb_Tab' for update;

select * from zc_role_search_condition s where s.condition_id like 'ZcDingDian_Tab1' for update;
--gys_normal gys_huiyuan
select * from ZC_SYS_BILL_ELEMENT e where e.bill_type_code like 'ZC_DINGDIAN' FOR UPDATE;

select * from AS_NO_RULE    where COMPO_ID = 'ZC_EB_QB' for update;
--DD_CODE ZC_DINGDIAN_GEN
SELECT * FROM AS_NUM_TOOL T WHERE T.NUM_TOOL_ID like 'ZC_EB_QB_GEN%' for update;
--SF_OUT_INFO_REQ_CODE_GEN_ID
--ZC_EB_PROJ_GEN
SELECT * FROM AS_NUM_TOOL_NO N WHERE N.NUM_TOOL_ID like 'ZC_EB_QB_GEN%' for update;

select * from AS_NO_RULE_SEG r where r.compo_id='ZC_EB_QB' for update;

select * from as_val v where v.valset_id like 'ZC_VS_HUIYUAN_ATTACH_TYPE1' for update;

select * from as_option O WHERE O.OPT_ID LIKE 'ZC_OPT_EXPERT_FAKE_SELECT' for update;

select * from as_menu m where m.menu_id like 'ZC%' for update;
--SF
SELECT * FROM AS_MENU_COMPO MC WHERE MC.COMPO_ID LIKE 'ZC_EB_QB' FOR UPDATE;
--ZC_HUIYUAN
SELECT * FROM AP_MENU_COMPO MC WHERE MC.COMPO_ID LIKE 'ZC%' FOR UPDATE;

select * from as_file f where f.file_id='sf_dossier_wuzheng_template' for update;

select * from v_sf_user_func v where v.user_id='zhengjie' and v.FUNC_ID!='fwatch';

select * from as_role_num_lim r where r.compo_id='ZC_EB_QB' for update;
--SQL_CONDITION

select * from as_role_func r where r.role_id='CGZX_KY_ZH' and r.compo_id='ZC_EB_BULLETIN_BID_xjs' for update;

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
      a.table_name = 'ZC_DINGDIAN_ITEM'
      AND B.OWNER='DTRUN'
      AND A.column_name=B.COLUMN_NAME;

