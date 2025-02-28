#include "planner/logical_plan/logical_operator/schema.h"

#include "common/exception.h"

namespace kuzu {
namespace planner {

f_group_pos Schema::createGroup() {
    auto pos = groups.size();
    groups.push_back(std::make_unique<FactorizationGroup>());
    return pos;
}

void Schema::insertToScope(
    const std::shared_ptr<binder::Expression>& expression, f_group_pos groupPos) {
    assert(!expressionNameToGroupPos.contains(expression->getUniqueName()));
    expressionNameToGroupPos.insert({expression->getUniqueName(), groupPos});
    expressionsInScope.push_back(expression);
}

void Schema::insertToGroupAndScope(
    const std::shared_ptr<binder::Expression>& expression, f_group_pos groupPos) {
    assert(!expressionNameToGroupPos.contains(expression->getUniqueName()));
    expressionNameToGroupPos.insert({expression->getUniqueName(), groupPos});
    groups[groupPos]->insertExpression(expression);
    expressionsInScope.push_back(expression);
}

void Schema::insertToGroupAndScope(
    const binder::expression_vector& expressions, f_group_pos groupPos) {
    for (auto& expression : expressions) {
        insertToGroupAndScope(expression, groupPos);
    }
}

bool Schema::isExpressionInScope(const binder::Expression& expression) const {
    for (auto& expressionInScope : expressionsInScope) {
        if (expressionInScope->getUniqueName() == expression.getUniqueName()) {
            return true;
        }
    }
    return false;
}

binder::expression_vector Schema::getExpressionsInScope(f_group_pos pos) const {
    binder::expression_vector result;
    for (auto& expression : expressionsInScope) {
        if (getGroupPos(expression->getUniqueName()) == pos) {
            result.push_back(expression);
        }
    }
    return result;
}

binder::expression_vector Schema::getSubExpressionsInScope(
    const std::shared_ptr<binder::Expression>& expression) {
    binder::expression_vector results;
    if (isExpressionInScope(*expression)) {
        results.push_back(expression);
        return results;
    }
    for (auto& child : expression->getChildren()) {
        for (auto& subExpression : getSubExpressionsInScope(child)) {
            results.push_back(subExpression);
        }
    }
    return results;
}

std::unordered_set<f_group_pos> Schema::getDependentGroupsPos(
    const std::shared_ptr<binder::Expression>& expression) {
    std::unordered_set<f_group_pos> result;
    for (auto& subExpression : getSubExpressionsInScope(expression)) {
        result.insert(getGroupPos(subExpression->getUniqueName()));
    }
    return result;
}

std::unordered_set<f_group_pos> Schema::getGroupsPosInScope() const {
    std::unordered_set<f_group_pos> result;
    for (auto& expressionInScope : expressionsInScope) {
        result.insert(getGroupPos(expressionInScope->getUniqueName()));
    }
    return result;
}

std::unique_ptr<Schema> Schema::copy() const {
    auto newSchema = std::make_unique<Schema>();
    newSchema->expressionNameToGroupPos = expressionNameToGroupPos;
    for (auto& group : groups) {
        newSchema->groups.push_back(std::make_unique<FactorizationGroup>(*group));
    }
    newSchema->expressionsInScope = expressionsInScope;
    return newSchema;
}

void Schema::clear() {
    groups.clear();
    clearExpressionsInScope();
}

size_t Schema::getNumGroups(bool isFlat) const {
    auto result = 0u;
    for (auto groupPos : getGroupsPosInScope()) {
        result += groups[groupPos]->isFlat() == isFlat;
    }
    return result;
}

std::vector<binder::expression_vector> SchemaUtils::getExpressionsPerGroup(
    const binder::expression_vector& expressions, const Schema& schema) {
    std::vector<binder::expression_vector> result;
    result.resize(schema.getNumGroups());
    for (auto& expression : expressions) {
        auto groupPos = schema.getGroupPos(*expression);
        result[groupPos].push_back(expression);
    }
    return result;
}

f_group_pos SchemaUtils::getLeadingGroupPos(
    const std::unordered_set<f_group_pos>& groupPositions, const Schema& schema) {
    auto leadingGroupPos = INVALID_F_GROUP_POS;
    for (auto groupPos : groupPositions) {
        if (!schema.getGroup(groupPos)->isFlat()) {
            return groupPos;
        }
        leadingGroupPos = groupPos;
    }
    return leadingGroupPos;
}

void SchemaUtils::validateAtMostOneUnFlatGroup(
    const std::unordered_set<f_group_pos>& groupPositions, const Schema& schema) {
    auto hasUnFlatGroup = false;
    for (auto groupPos : groupPositions) {
        if (!schema.getGroup(groupPos)->isFlat()) {
            if (hasUnFlatGroup) {
                throw common::InternalException(
                    "Unexpected multiple unFlat factorization groups found.");
            }
            hasUnFlatGroup = true;
        }
    }
}

void SchemaUtils::validateNoUnFlatGroup(
    const std::unordered_set<f_group_pos>& groupPositions, const Schema& schema) {
    for (auto groupPos : groupPositions) {
        if (!schema.getGroup(groupPos)->isFlat()) {
            throw common::InternalException("Unexpected unFlat factorization group found.");
        }
    }
}

} // namespace planner
} // namespace kuzu
