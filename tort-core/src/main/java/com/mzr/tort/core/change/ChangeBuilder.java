package com.mzr.tort.core.change;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.mzr.tort.core.Identified;
import com.mzr.tort.core.change.model.Change;
import com.mzr.tort.core.change.model.ObjectChange;
import com.mzr.tort.core.change.model.ValueChange;
import com.mzr.tort.core.domain.DateHelper;
import com.mzr.tort.core.domain.EnumedDictionary;
import com.mzr.tort.core.dto.EnumedDictionaryDto;
import com.mzr.tort.core.dto.utils.DtoUtils;
import com.mzr.tort.core.dto.utils.Prop;
import com.mzr.tort.core.dto.utils.TypeUtils;
import com.mzr.tort.core.mapper.TortConfigurableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;

public class ChangeBuilder {
    
    private static final String CHANGE_PREFIX = "change";
    private static final Set<String> EXCLUDE_PROPERTY_NAMES = Collections.unmodifiableSet(Sets.newHashSet("id", "class"));
    
    @Autowired
    private ApplicationContext appContext;
    
    @Autowired
    private TortConfigurableMapper tortConfigurableMapper;
    
    private String noValueCaption = "";
    
    private SimpleDateFormat dateFormater = new SimpleDateFormat("dd.MM.yy HH:mm");

    public ObjectChange buildChange(Optional<? extends Identified> oldElement, Optional<? extends Identified> youngElement,
                                    String aNamespace) {
        String changeNamespace = Joiner.on(".").join(CHANGE_PREFIX, aNamespace);
        List<Change> childs = buildChanges(oldElement, youngElement, obtainClass(oldElement, youngElement),
                changeNamespace);
        String parentCaption = appContext.getMessage(changeNamespace, new Object[]{}, LocaleContextHolder.getLocale());
        return new ObjectChange(parentCaption, childs);
    }
    

    public String getNoValueCaption() {
        return noValueCaption;
    }

    public void setNoValueCaption(String aNoValueCaption) {
        noValueCaption = aNoValueCaption;
    }

    public SimpleDateFormat getDateFormater() {
        return dateFormater;
    }

    public void setDateFormater(SimpleDateFormat aDateFormater) {
        dateFormater = aDateFormater;
    }

    private List<Change> buildChanges(Optional<?> aOld, Optional<?> aYoung, Class<?> parentClass, String parentPath) {
        List<Change> result = new ArrayList<>();
        List<Prop> props = DtoUtils.getAllProps(parentClass);
        for (Prop dtoProp: props) {
            Optional oldVal = aOld.map(dtoProp::readProperty);
            Optional youngVal = aYoung.map(dtoProp::readProperty);
            String valueCaption = getValueCaption(parentPath, dtoProp);
            String currentPath = Joiner.on(".").join(parentPath, dtoProp.getName());
            Class<?> propertyType = dtoProp.getPropertyType();
            ChangeObject changeAnnotation = propertyType.getAnnotation(ChangeObject.class);
            if (Objects.nonNull(changeAnnotation) || Identified.class.isAssignableFrom(propertyType)) {
                ChangableDictionary changableDictionary = dtoProp.findAnnotation(ChangableDictionary.class);
                if (changableDictionary != null && isDictionaryChanged(oldVal, youngVal)) {                    
                    String oldCaption = DtoUtils.readProperty(changableDictionary.value(), oldVal);
                    String youngCaption = DtoUtils.readProperty(changableDictionary.value(), youngVal);
                    result.add(new ValueChange(valueCaption, oldCaption, youngCaption));
                } else {
                    buildChanges(oldVal, youngVal, dtoProp.getPropertyType(), currentPath);
                }
            } else if (Collection.class.isAssignableFrom(dtoProp.getPropertyType())) {
                Class elementClass = TypeUtils.getTypeArgument(
                        dtoProp.getReadMethod().getGenericReturnType(),
                        Collection.class);
                Collection oldCollection = (Collection) oldVal.orElse(Collections.emptyList());
                Collection youngCollection = (Collection) youngVal.orElse(Collections.emptyList());
                
                if (Identified.class.isAssignableFrom(elementClass)) {                    
                    List<Change> changedElements = buildIndetifiedCollection(currentPath, oldCollection, youngCollection);
                    if (!changedElements.isEmpty()) {
                        result.add(new ObjectChange(valueCaption, changedElements));
                    }
                } else {
                    List<Change> changedElements = buildSimpleCollection(currentPath, oldCollection, youngCollection,
                            dtoProp);
                    if (!changedElements.isEmpty()) {
                        result.add(new ObjectChange(valueCaption, changedElements));
                    }
                }
            } else if (!shouldExclude(dtoProp) && isSimpleChanged(oldVal, youngVal)) {
                result.add(buildSimpleValueChange(oldVal, youngVal, valueCaption));
            }
        }
        return result;
    }

    private ValueChange buildSimpleValueChange(Optional oldVal, Optional youngVal, String valueCaption) {
        Class valueClass = obtainClass(oldVal, youngVal);
        String oldString;
        String youngString;
        if (EnumedDictionary.class.isAssignableFrom(valueClass)) {
            oldString = (String) oldVal
                    .map(o-> tortConfigurableMapper.map(oldVal, EnumedDictionaryDto.class).getCaption())
                    .orElse(noValueCaption);
            youngString = (String) youngVal
                    .map(o-> tortConfigurableMapper.map(youngVal, EnumedDictionaryDto.class).getCaption())
                    .orElse(noValueCaption);
        } else if (Date.class.isAssignableFrom(valueClass)) {
            oldString = (String) oldVal.map(o-> DateHelper.dateTimeFormat().format((Date)o)).orElse(noValueCaption);
            youngString = (String) youngVal.map(o->DateHelper.dateTimeFormat().format((Date)o)).orElse(noValueCaption);
        } else {
            oldString = (String) oldVal.map(o->o.toString()).orElse(noValueCaption); 
            youngString = (String) youngVal.map(y->y.toString()).orElse(noValueCaption);
        }
         
        ValueChange simpleValueChange = new ValueChange(valueCaption, oldString, youngString);
        return simpleValueChange;
    }

    private List<Change> buildSimpleCollection(String parentPath, Collection oldCollection,
            Collection youngCollection, Prop aDtoProp) {
        Set<?> oldOrpahns = new java.util.HashSet<>(oldCollection);
        oldOrpahns.removeAll(youngCollection);
        Set<?> youngOrpahns = new java.util.HashSet<>(youngCollection);
        youngOrpahns.removeAll(oldCollection);
        List<Change> changedElements = new ArrayList<>();
        int elementNum = 0;
        for (Object oldElement : oldOrpahns) {
            changedElements.add(buildSimpleValueChange(Optional.of(oldElement), Optional.empty(),
                    String.valueOf(++elementNum)));
        }
        for (Object youngElement : youngOrpahns) {
            changedElements.add(buildSimpleValueChange(Optional.empty(), Optional.of(youngElement),
                    String.valueOf(++elementNum)));
        }
        return changedElements;
    }

    private List<Change> buildIndetifiedCollection(String parentPath, Collection oldCollection,
            Collection youngCollection) {
        Set<Serializable> ids = new HashSet<>();
        Collection<? extends Identified> oldIdDtoCollection = oldCollection;
        Collection<? extends Identified> youngIdDtoCollection = youngCollection;
        oldIdDtoCollection.stream().map(Identified::getId).forEach(ids::add);
        youngIdDtoCollection.stream().map(Identified::getId).forEach(ids::add);
        
        int elementNum = 0;
        List<Change> changedElements = new ArrayList<>();
        for (Serializable id : ids) {
            Optional<? extends Identified> oldElement = oldIdDtoCollection.stream()
                    .filter(i->Objects.equals(id, i.getId()))
                    .findFirst();
            Optional<? extends Identified> youngElement = youngIdDtoCollection.stream()
                    .filter(i->Objects.equals(id, i.getId()))
                    .findFirst();
            Class elementClass = obtainClass(oldElement, youngElement);
            
            List<Change> elementChanges = buildChanges(oldElement, youngElement, elementClass, parentPath);
            if (!elementChanges.isEmpty()) {
                changedElements.add(new ObjectChange(String.valueOf(++elementNum), elementChanges));
            }
        }
        return changedElements;
    }

    private Class obtainClass(Optional<?> oldElement, Optional<?> youngElement) {
        Optional<Class> optClass = oldElement.map(o->(Class)o.getClass());
        Class elementClass = optClass.orElseGet(()->youngElement.get().getClass());
        return elementClass;
    }

    private boolean shouldExclude(Prop aDtoProp) {
        return aDtoProp.hasAnnotation(ChangeIgnore.class) || EXCLUDE_PROPERTY_NAMES.contains(aDtoProp.getName());
    }

    private boolean isSimpleChanged(Optional<?> aOldVal, Optional<?> aYoungVal) {
        return !Objects.equals(aOldVal, aYoungVal);
    }

    private boolean isDictionaryChanged(Optional<?> aOldVal, Optional<?> aYoungVal) {
        Optional<Serializable> oldId = aOldVal.map(o->DtoUtils.readProperty("id", o)) ;
        Optional<Serializable> youngId = aYoungVal.map(y->DtoUtils.readProperty("id", y));
        return !Objects.equals(oldId, youngId);
    }

    private String getValueCaption(String aParentPath, Prop aDtoProp) {
        String path = Joiner.on(".").skipNulls().join(aParentPath, aDtoProp.getName());
        return appContext.getMessage(path, new Object[]{}, LocaleContextHolder.getLocale());
    }
    
}
