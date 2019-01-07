select * 
from records r
inner join (
  select id,max(commentdate) from comments group by recordid) groupedcomment on groupedcomment.recordid
inner join comments 

select * from records where ROOMNUMBER is not null

select rooms.ROOMNUMBER,r.id ,c.commentdate,c.comment
from records r
right outer join rooms on r.ROOMNUMBER = rooms.ROOMNUMBER
left join comments c on c.recordid=r.id
left join (select recordid,max(commentdate) as commentdate from comments group by recordid) gc on ( gc.recordid = r.id and gc.commentdate = c.commentdate)

select id,max(commentdate) as commentdate from comments group by id

select * from comments