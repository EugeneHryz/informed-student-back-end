package edu.example.dto.user;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import edu.example.dto.PageDto;
import edu.example.model.QUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserRequestDto extends PageDto {
    /**
     * Search by username or email
     */
    private String searchTerm;
    private Boolean isBanned;

    public Predicate toPredicate() {
        var user = QUser.user;
        List<Predicate> predicates = new ArrayList<>();

        if (isNotBlank(searchTerm)) {
            predicates.add(user.username.containsIgnoreCase(searchTerm).or(user.email.containsIgnoreCase(searchTerm)));
        }
        if (nonNull(isBanned)) {
            predicates.add(user.isBanned.eq(isBanned));
        }

        if (predicates.isEmpty()) {
            return Expressions.TRUE.isTrue();
        }
        return ExpressionUtils.allOf(predicates);
    }
}
