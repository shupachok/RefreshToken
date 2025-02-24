package com.sp.refreshtoken.entity.base;


import com.sp.refreshtoken.util.AppUtil;
import com.sp.refreshtoken.util.DateUtil;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

@Log4j2
@Component
public class EntityListener {

    @PrePersist
    public void prePersistFunction(Object object) {
        this.assignValueToCommonFields(object, "CREATE");
    }

    @PreUpdate
    public void preUpdateFunction(Object object) {
        this.assignValueToCommonFields(object,"UPDATE");
    }

    @SneakyThrows
    private void assignValueToCommonFields(Object arg, String status) {

        String user = null;
        Authentication authen = SecurityContextHolder.getContext().getAuthentication();
        if (AppUtil.isNotNull(authen) && authen.getPrincipal() != "anonymousUser") {
            UserDetails userDetails = (UserDetails) authen.getPrincipal();
            if (AppUtil.isNotNull(userDetails) && AppUtil.isNotNull(userDetails.getUsername())) {
                user = userDetails.getUsername();
            }
        }

        if (status.equals("CREATE")) {
            BeanUtils.setProperty(arg, "createBy", user != null ? user : "SYSTEM");
            BeanUtils.setProperty(arg, "createDate", DateUtil.getCurrentDate());
        }else{
            BeanUtils.setProperty(arg, "updateBy", user != null ? user : "SYSTEM");
            BeanUtils.setProperty(arg, "updateDate", DateUtil.getCurrentDate());
        }

        Class<?> cls = arg.getClass();
        for (Field field : cls.getDeclaredFields()) {
            
            Field strField = ReflectionUtils.findField(cls, field.getName());
            if (strField.getType().equals(String.class)) {

                strField.setAccessible(true);
                Object value = ReflectionUtils.getField(strField, arg);

                if (AppUtil.isNotNull(value) && AppUtil.isEmpty(value.toString())) {
                    ReflectionUtils.makeAccessible(strField); //set null when emptyString
                    ReflectionUtils.setField(strField, arg, null);
                }
            }
        }
    }
}
