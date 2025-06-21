package com.min.holidaykeeper.repository;

import com.min.holidaykeeper.dto.request.HolidayRequest;
import com.min.holidaykeeper.entity.Holiday;
import com.min.holidaykeeper.entity.QHoliday;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HolidayCustomRepositoryImpl implements HolidayCustomRepository {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public Page<Holiday> searchHolidays(HolidayRequest request, Pageable pageable) {
        QHoliday holiday = QHoliday.holiday;
        
        JPAQuery<Holiday> query = queryFactory
                .selectFrom(holiday)
                .leftJoin(holiday.types).fetchJoin()
                .where(
                    yearEq(request.getHolidayYear()),
                    countryCodeEq(request.getCountryCode()),
                    dateGoe(request.getFrom()),
                    dateLoe(request.getTo()),
                    typeContains(request.getType())
                )
                .distinct();
        
        for (Sort.Order order : pageable.getSort()) {
            OrderSpecifier<?> orderSpecifier = getOrderSpecifier(order, holiday);
            if (orderSpecifier != null) {
                query.orderBy(orderSpecifier);
            }
        }
        
        Long totalCount = queryFactory
                .select(holiday.count())
                .from(holiday)
                .where(
                    yearEq(request.getHolidayYear()),
                    countryCodeEq(request.getCountryCode()),
                    dateGoe(request.getFrom()),
                    dateLoe(request.getTo()),
                    typeContains(request.getType())
                )
                .fetchOne();
        
        List<Holiday> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        return new PageImpl<>(content, pageable, totalCount != null ? totalCount : 0L);
    }
    
    private BooleanExpression yearEq(Integer year) {
        return year != null ? QHoliday.holiday.holidayYear.eq(year) : null;
    }
    
    private BooleanExpression countryCodeEq(String countryCode) {
        return StringUtils.hasText(countryCode) ? QHoliday.holiday.countryCode.eq(countryCode) : null;
    }
    
    private BooleanExpression dateGoe(LocalDate from) {
        return from != null ? QHoliday.holiday.date.goe(from) : null;
    }
    
    private BooleanExpression dateLoe(LocalDate to) {
        return to != null ? QHoliday.holiday.date.loe(to) : null;
    }
    
    private BooleanExpression typeContains(String type) {
        return StringUtils.hasText(type) ? QHoliday.holiday.types.contains(type) : null;
    }
    
    private OrderSpecifier<?> getOrderSpecifier(Sort.Order order, QHoliday holiday) {
        String property = order.getProperty();
        Sort.Direction direction = order.getDirection();
        
        switch (property) {
            case "date":
                return direction.isAscending() ? holiday.date.asc() : holiday.date.desc();
            case "id":
                return direction.isAscending() ? holiday.id.asc() : holiday.id.desc();
            case "name":
                return direction.isAscending() ? holiday.name.asc() : holiday.name.desc();
            case "countryCode":
                return direction.isAscending() ? holiday.countryCode.asc() : holiday.countryCode.desc();
            default:
                return null;
        }
    }
}
