select * from tasks t
join token_usage tu on t.id =  tu.task_id;
