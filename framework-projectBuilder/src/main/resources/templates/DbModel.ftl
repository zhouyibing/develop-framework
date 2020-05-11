package ${object.packageStr}.model.db;
import lombok.Data;
import java.io.Serializable;
import com.yipeng.framework.common.model.db.BaseModel;
import javax.persistence.*;
<#if (object.importList)??>
	<#list object.importList as item>
import ${item};
	</#list>
</#if>
/**
* ${object.comment}
* @author ${base.authorName}
* email:${base.authorEmail}
* AUTO-GENERATE BY PROJECT-BUILDER
**/
@Data
@Table(name = "${object.originalTableName}")
public class ${object.name}Model extends BaseModel<${object.primaryKeyType}> implements Serializable {
	private static final long serialVersionUID = ${object.serialId}L;

	<#if (object.fields)??>
		<#list object.fields as item>
		<#if item.notInModel == false>

			<#if (item.fieldComment)??>
	/** ${item.fieldComment} */
			</#if>
	@Column(name = "${item.originalFieldName}")
	private ${item.fieldType} ${item.fieldName};
		</#if>
		</#list>
	</#if>
	<#if (object.primaryKeyName)??>
	public String primaryKeyName() {
		return "${object.primaryKeyName!}";
	}
	</#if>
}