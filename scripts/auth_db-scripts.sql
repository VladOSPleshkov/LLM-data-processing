--select * from users u join user_roles ur on u.id = ur.assigned_by;
select * from roles r join user_roles u on r.id = u.role_id;