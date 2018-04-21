package com.how2java.tmall.service.impl;

import com.how2java.tmall.mapper.PropertyValueMapper;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.pojo.PropertyValue;
import com.how2java.tmall.pojo.PropertyValueExample;
import com.how2java.tmall.service.PropertyService;
import com.how2java.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyValueServiceImpl implements PropertyValueService {
    @Autowired
    PropertyValueMapper propertyValueMapper;
    @Autowired
    PropertyService propertyService;

    @Override
    public void init(Product p) {
//        根据分类id获得该分类下的所有属性
        List<Property> properties = propertyService.list(p.getCid());
        for (Property property :
                properties) {
//            针对该产品，对每个属性进行一次查询，若无记录，则创建一个并添加
            PropertyValue propertyValue = get(property.getId(), p.getId());
            if (propertyValue == null) {
                PropertyValue value = new PropertyValue();
                value.setPid(p.getId());
                value.setPtid(property.getId());
                propertyValueMapper.insert(value);
            }
        }
    }

    @Override
    public void update(PropertyValue pv) {
        propertyValueMapper.updateByPrimaryKeySelective(pv);
    }

    @Override
    public PropertyValue get(int ptid, int pid) {
        PropertyValueExample example = new PropertyValueExample();
        example.createCriteria().andPtidEqualTo(ptid).andPidEqualTo(pid);
        List<PropertyValue> propertyValues = propertyValueMapper.selectByExample(example);
        if (propertyValues.isEmpty()) {
            return null;
        }
        return propertyValues.get(0);
    }

    @Override
    public List<PropertyValue> list(int pid) {
//        获取某产品下的所有属性值
        PropertyValueExample example = new PropertyValueExample();
        example.createCriteria().andPidEqualTo(pid);
        List<PropertyValue> propertyValues = propertyValueMapper.selectByExample(example);
//        设置每个属性值对象的property属性
        for (PropertyValue propertyValue :
                propertyValues) {
            Property property = propertyService.get(propertyValue.getPtid());
            propertyValue.setProperty(property);
        }
        return propertyValues;
    }
}
