package com.fido.framework.core.web.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.fido.framework.core.api.BaseApi;
import com.fido.framework.core.constants.Constants;
import com.fido.framework.core.exception.ErrorCode;
import com.fido.framework.core.exception.ExceptionUtil;
import com.fido.framework.core.model.biz.*;
import com.fido.framework.core.param.PageParam;
import com.fido.framework.core.result.Result;
import com.fido.framework.core.service.BaseService;
import com.fido.framework.core.utils.Precondition;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.validation.Valid;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author: yibingzhou
 */
public class BaseController<P extends BaseParam, R, S extends BaseService> implements BaseApi<P,R> {
    protected final static String ALL = "*";

    @Autowired
    @JsonIgnore
    protected S service;

    private Class resultClass;

    private Map<String, Set<Intensifier>> intensifierMap = new HashMap<>();
    private Comparator<Intensifier> intensifierComparable = (o1, o2) -> {
        if (o1.getPriority().equals(o2.getPriority())) {
            return 0;
        }
        return o1.getPriority() > o2.getPriority() ? -1 : 1;
    };

    protected void addIntensifier(Intensifier intensifier) {
        if (intensifier == null) {
            return;
        }
        Set<Intensifier> intensifiers = intensifierMap.get(intensifier.getName());
        if(null == intensifiers) {
            intensifiers = Sets.newTreeSet(intensifierComparable);
            intensifierMap.put(intensifier.getName(), intensifiers);
        }
        intensifiers.add(intensifier);
    }

    private Class getResultClass() {
        if (resultClass != null ) {
            return resultClass;
        }
        try {
            ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
            resultClass = (Class<R>) pt.getActualTypeArguments()[1];
            return resultClass;
        } catch (Exception e) {
            throw ExceptionUtil.doThrow(ErrorCode.SERVER_INTERNAL_ERROR.msg(e.getMessage()));
        }
    }
    /**
     * 从header里获取token
     * @return
     */
    public String getAccessToken() {
        String token = getHeader(Constants.HEAD_TOKEN);
        if (StringUtils.isEmpty(token)) {
            ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            token = sra.getRequest().getParameter(Constants.HEAD_TOKEN);
        }
        return token;
    }

    /**
     * 从请求里获取指定名称的header
     * @return
     */
    public String getHeader(String name) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String value = sra.getRequest().getHeader(name);
        return value;
    }

    @Override
    @PostMapping("/createIfAbsent")
    @ApiOperation("不存在时创建")
    public Result<Boolean> createIfAbsent(@Valid @RequestBody P param) {
        checkManagedParam(param);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.createIfAbsent(p,p));
    }

    @Override
    @PostMapping("/create")
    @ApiOperation("创建")
    public Result<Boolean> create(@Valid @RequestBody P param) {
        checkManagedParam(param);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.create(p));
    }

    @Override
    @PostMapping("/save")
    @ApiOperation("保存")
    public Result<Boolean> save(@Valid @RequestBody P param) {
        checkUpdater(param);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.save(p));
    }

    protected  <T, R> Result enhance(String intensifierName, T param, Function<T, R> main) {
        Object ret = enhanceNoResultWrapped(intensifierName, param, main);
        return Result.success(ret);
    }
    protected <T,R> Object enhanceNoResultWrapped(String intensifierName, T param, Function<T, R> main) {
        //核心逻辑调用前的处理
        Set<Intensifier> myIntensifiers = intensifierMap.get(intensifierName);
        Set<Intensifier> allIntensifiers = intensifierMap.get(ALL);
        Set<Intensifier> intensifiers = Sets.newTreeSet(intensifierComparable);
        if (allIntensifiers != null) {
            intensifiers.addAll(allIntensifiers);
        }
        if (myIntensifiers != null) {
            intensifiers.addAll(myIntensifiers);
        }
        if(CollectionUtil.isNotEmpty(intensifiers)) {
            for(Intensifier intensifier: intensifiers) {
                Object ret = intensifier.getBefore() == null ? null : intensifier.getBefore().apply(param);
                if(ret != null && intensifier.isUseBeforeEnhanceResult()) {
                    if (!param.getClass().isAssignableFrom(ret.getClass())) {
                        throw ExceptionUtil.doThrow(ErrorCode.PARAM_TYPE_NOT_MATCH.msg("前置处理返回结果类型必须是传入参数类型同类或子类"));
                    }
                    param = (T) ret;
                }
            }
        }
        R res = main.apply(param);
        //核心逻辑调用后的处理
        if(CollectionUtil.isNotEmpty(allIntensifiers)) {
            for(Intensifier intensifier: allIntensifiers) {
                Object ret = intensifier.getAfter() == null ? null : intensifier.getAfter().apply(param);
                if(ret != null && intensifier.isUseBeforeEnhanceResult()) {
                    param = (T) ret;
                }
            }
        }
        return res;
    }

    @Override
    @PostMapping("/saveList")
    @ApiOperation("批量保存")
    public Result<Integer> saveList(@Valid @RequestBody ValidList<P> params) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, params, (p) -> {
            checkUpdater(p);
            return service.save(p);
        });
    }

    @Override
    @PostMapping("/delete")
    @ApiOperation("删除")
    public Result<Integer> delete(@Valid @RequestBody P param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.deleteAllMatch(p));
    }

    @Override
    @GetMapping("/deleteByPk")
    @ApiOperation("根据主键删除")
    public <K extends Comparable> Result<Boolean> deleteByPk(@ApiParam(name = "pk", value = "主键", required = true) @RequestParam K pk) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, pk, (p) -> service.deleteByPk(p));
    }

    @Override
    @PostMapping("/deleteByPkList")
    @ApiOperation("批量根据主键删除")
    public <K extends Comparable> Result<Integer> deleteByPkList(@ApiParam(name = "pk", value = "主键列表", required = true) @RequestBody List<K> pks) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, pks, (p) -> service.deleteByPks(p));
    }

    @Override
    @PostMapping("/logicDelete")
    @ApiOperation("逻辑删除")
    public Result<Integer> logicDelete(@RequestBody P param) {
        checkUpdater(param);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.logicDeleteAllMatch(p));
    }

    @Override
    @GetMapping("/logicDeleteByPk")
    @ApiOperation("根据主键逻辑删除")
    public <K extends Comparable> Result<Boolean> logicDeleteByPk(@ApiParam(name = "pk", value = "主键", required = true) @RequestParam K pk) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, pk, (p) -> service.logicDeleteByPk(p));
    }

    @Override
    @PostMapping("/logicDeleteByPkList")
    @ApiOperation("批量根据主键逻辑删除")
    public <K extends Comparable> Result<Integer> logicDeleteByPkList(@ApiParam(name = "pk", value = "主键列表", required = true) @RequestBody List<K> pks) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, pks, (p) -> service.logicDeleteByPks(p));
    }

    @Override
    @PostMapping("/update")
    @ApiOperation("更新")
    public Result<Boolean> update(@Valid @RequestBody P param) {
        checkUpdater(param);
        Comparable pkValue = getPk(param);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.update(pkValue, p));
    }

    @Override
    @PostMapping("/updateList")
    @ApiOperation("批量更新")
    public Result<ResultOverview> updateList(@Valid @RequestBody ValidList<P> param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> {
            checkUpdater(p);
            AtomicInteger failed = new AtomicInteger(0);
            AtomicInteger success = new AtomicInteger(0);
            List<Comparable> list = getPks(param);
            for(int i=0;i<list.size();i++) {
                if(service.update(list.get(i), param.get(i))) {
                    success.incrementAndGet();
                } else {
                    failed.incrementAndGet();
                }
            }
            ResultOverview resultOverview = new ResultOverview();
            resultOverview.setFail(failed.get());
            resultOverview.setSuccess(success.get());
            return resultOverview;
        });
    }

    @Override
    @GetMapping("/getByPk")
    @ApiOperation("根据主键查询")
    public <K extends Comparable> Result<R> getByPk(@ApiParam(name = "pk", value = "主键",required = true) @RequestParam K pk) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, pk, (p) -> service.queryByPk(p, getResultClass()));
    }

    @Override
    @PostMapping("/getByPks")
    @ApiOperation("批量根据主键查询")
    public <K extends Comparable> Result<R> getByPkList(@ApiParam(name = "pks", value = "主键列表", required = true) @RequestBody List<K> pks) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, pks, (p) -> service.queryByPks(p,getResultClass()));
    }

    @Override
    @PostMapping("/get")
    @ApiOperation("条件查询")
    public Result<R> get(@Valid @RequestBody P param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.queryAllMatch(p,getResultClass()));
    }

    @Override
    @PostMapping("/page")
    @ApiOperation("分页查询")
    public PageInfo<R> page(@Valid @RequestBody PageParam<P> pageParam) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return (PageInfo) enhanceNoResultWrapped(methodName, pageParam, (p) -> service.pageAllMatch(p.getParams(),p.getOrderBy(), p.getOrderType(), p.getCurrent(), p.getPageSize(), getResultClass()));
    }

    private Comparable getPk(P param) {
        Precondition.checkNotNull(param, "主键不能为空");
        Object pkValue  = ReflectUtil.getFieldValue(param, param.primaryKey());
        Precondition.checkNotNull(pkValue, "主键不能为空");
        return (Comparable) pkValue;
    }

    private List<Comparable> getPks(List<P> pks) {
        Precondition.checkNotEmpty(pks, "主键列表不能为空");
        List<Comparable> list = Lists.newArrayList();
        pks.forEach(pk -> {
            Object pkValue = ReflectUtil.getFieldValue(pk, pk.primaryKey());
            Precondition.checkNotNull(pkValue, "主键不能为空");
            list.add((Comparable) pkValue);
        });
        return list;
    }

    protected void checkManagedParam(Object param) {
        if( param instanceof ManagedParam) {
            checkManagedParam((ManagedParam) param);
        }
    }

    protected void checkManagedParam(ManagedParam param) {
        Precondition.checkNotBlank(param.getCreatorId(), "创建人不能为空");
        checkUpdater(param);
    }

    protected void checkUpdater(Object param) {
        if( param instanceof ManagedParam) {
            checkUpdater((ManagedParam) param);
        }
    }

    protected void checkUpdater(ManagedParam param) {
        Precondition.checkNotBlank(param.getUpdaterId(), "更新人不能为空");
    }
}
