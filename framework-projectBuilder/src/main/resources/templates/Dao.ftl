package ${object.packageStr}.dao;
import ${object.packageStr}.mapper.${object.name}Mapper;
import ${object.packageStr}.model.db.${object.name}Model;
import com.fido.framework.core.dao.BaseDao;
import org.springframework.stereotype.Repository;

/**
* @author ${base.authorName}
* email:${base.authorEmail}
* AUTO-GENERATE BY PROJECT-BUILDER
**/
@Repository
public class ${object.name}Dao extends BaseDao<${object.name}Model, ${object.name}Mapper> {
}