-- drop the dependents which are generated: 
drop table test_100.ObjectSamplers;
drop table test_100.RelationalSamplers;
drop type def_100.DataTypeSampler;
drop type def_100.MemoTable;
drop type def_100.Memo;

-- Generated on Fri Nov 23 14:52:56 PST 2001
create type Def_100.Memo as object (
  author varchar2(80),
  subject varchar2(80),
  body varchar2(4000),
  recorded date
);

-- Generated on Fri Nov 23 14:52:56 PST 2001
create type Def_100.MemoTable as table of Def_100.Memo;



-- Generated on Fri Nov 23 14:52:56 PST 2001
create type Def_100.DataTypeSampler as object (
  textMapping varchar2(80),
  charMapping char(16),
  numberMapping number(8),
  byteMapping number(3,0),
  integerMapping number(10,0),
  longMapping number(19,0),
  doubleMapping number,
  timestampMapping date,
  dateMapping date,
  timeMapping date,
  blobMapping blob,
  clobMapping clob,
  objectMapping Def_100.Memo,
  refMapping ref Def_100.Memo,
  arrayMapping Def_100.MemoTable,
  booleanMapping number(1,0)
);


-- Generated on Fri Nov 23 14:52:56 PST 2001
create table test_100.RelationalSamplers (
  textMapping varchar2(80), 
  charMapping char(16), 
  numberMapping number(8), 
  byteMapping number(3,0), 
  integerMapping number(10,0), 
  longMapping number(19,0), 
  doubleMapping number, 
  timestampMapping date, 
  dateMapping date, 
  timeMapping date, 
  blobMapping blob, 
  clobMapping clob, 
  objectMapping Def_100.Memo, 
  refMapping ref Def_100.Memo, 
  arrayMapping Def_100.MemoTable, 
  booleanMapping number(1,0) 
)    
    nested table arrayMapping 
    store as arrayMappingRelationalSamplers
;
alter table test_100.RelationalSamplers
  add primary key (textMapping);
alter table test_100.RelationalSamplers
  add ( check (charMapping is not null) );

-- Generated on Fri Nov 23 14:52:56 PST 2001
create table test_100.ObjectSamplers of Def_100.DataTypeSampler
    (textMapping primary key)
    nested table arrayMapping 
    store as arrayMappingObjectSamplers
;
alter table test_100.ObjectSamplers
  add ( check (charMapping is not null) );
