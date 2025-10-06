package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDao dao = DaoFactory.createDepartmentDao();
	
	public List<Department> findAll() {
		return dao.findAll();
	}
	
	public void saveOrUpdate(Department obj) { // VERIFICAR SE TENHO QUE INSERIR OU ATUALIZAR O DEPARTAMENTO NO BANCO
		if (obj.getId() == null) {
			dao.insert(obj);
		// SE ESSE OBJETO TEM O ID IGUAL A NULO, SIGNIFICA QUE ESTOU INSERINDO UM NOVO DEPARTAMENTO
		}
		else {
			dao.update(obj);
		// SE FOR UM DEPARTAMENTO QUE JÁ TEM ID, SIGNIFICA QUE O DEPARTAMENTO JÁ EXISTE E VAI SER ATUALIZADO
		}
	}
}
