drop index idx_text__code_type;
create index idx_text__code_type on stk_text (code,type);
