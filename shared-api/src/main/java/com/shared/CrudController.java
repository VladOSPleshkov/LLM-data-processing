package com.shared;

public interface CrudController<C, T, U, ID> extends Controller {
    String processCreate(C request);
    String processGetById(ID id);
    String processGetAll();
    String processUpdate(ID id, U request);
    String processDelete(ID id);
}