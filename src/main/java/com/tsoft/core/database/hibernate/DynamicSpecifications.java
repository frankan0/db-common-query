package com.tsoft.core.database.hibernate;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.List;

public class DynamicSpecifications {

	public static <T> Specification<T> bySearchFilter(
			final Collection<SearchFilter> filters, final Class<T> clazz) {
		return new Specification<T>() {

			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				if (!CollectionUtils.isEmpty(filters)) {

					List<Predicate> predicates = Lists.newArrayList();
					for (SearchFilter filter : filters) {
						// nested path translate, 如Task的名为"user.name"的filedName,
						// 转换为Task.user.name属性
						String[] names = StringUtils.split(filter.fieldName,
								".");
						Path expression = root.get(names[0]);
						for (int i = 1; i < names.length; i++) {
							expression = expression.get(names[i]);
						}

						// logic operator
						switch (filter.operator) {
						case EQ:
							predicates.add(builder.equal(expression,
									filter.value));
							break;
						case LIKE:
							predicates.add(builder.like(expression, "%"
									+ filter.value + "%"));
							break;
						case GT:
							predicates.add(builder.greaterThan(expression,
									(Comparable) filter.value));
							break;
						case LT:
							predicates.add(builder.lessThan(expression,
									(Comparable) filter.value));
							break;
						case GTE:
							predicates.add(builder.greaterThanOrEqualTo(
									expression, (Comparable) filter.value));
							break;
						case LTE:
							predicates.add(builder.lessThanOrEqualTo(
									expression, (Comparable) filter.value));
							break;
						}
					}

					// 将所有条件用 and 联合起来
					if (predicates.size() > 0) {
						return builder.or(predicates
								.toArray(new Predicate[predicates.size()]));
					}
				}
				return builder.conjunction();
			}

		};
	}
}