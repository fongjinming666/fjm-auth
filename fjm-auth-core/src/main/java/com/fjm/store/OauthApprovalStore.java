package com.fjm.store;

import com.fjm.dao.IOauthApprovalDao;
import com.fjm.entity.OauthApproval;
import com.fjm.properties.AuthProperties;
import com.fjm.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-18 下午4:03
 * @Description:
 */
@Slf4j
public class OauthApprovalStore implements ApprovalStore {

    @Resource
    private IOauthApprovalDao oauthApprovalDao;

    @Resource
    private AuthProperties authProperties;

    private boolean handleRevocationsAsExpiry = false;

    @Override
    public boolean addApprovals(Collection<Approval> approvals) {
        boolean isSuccess = true;
        Iterator<Approval> iterator = approvals.iterator();
        while (iterator.hasNext()) {
            Approval approval = iterator.next();
            if (!updateApproval(approval) && !addApproval(approval)) {
                isSuccess = false;
            }
        }
        return isSuccess;
    }

    private boolean updateApproval(Approval approval) {
        Query query = new Query();
        query.addCriteria(Criteria.where(OauthApproval.USER_ID).is(approval.getUserId()));
        query.addCriteria(Criteria.where(OauthApproval.CLIENT_ID).is(approval.getClientId()));
        query.addCriteria(Criteria.where(OauthApproval.SCOPE).is(approval.getScope()));

        Update update = new Update();
        update.set(OauthApproval.EXPIRE_AT, approval.getExpiresAt().getTime());
        update.set(OauthApproval.STATUS, approval.getStatus() != null ? approval.getStatus() : Approval.ApprovalStatus.APPROVED);
        update.set(OauthApproval.LAST_MODIFIED_AT, approval.getLastUpdatedAt().getTime());

        long updateCount = oauthApprovalDao.update(query, update);
        return (updateCount > 0);
    }

    private boolean addApproval(Approval approval) {
        OauthApproval oauthApproval = new OauthApproval();
        oauthApproval.setUserId(approval.getUserId());
        oauthApproval.setClientId(approval.getClientId());
        oauthApproval.setScope(approval.getScope());
        //oauthApproval.setExpireAt(approval.getExpiresAt().getTime());
        /** 默认一个月后，修改过期时间1天后. */
        Date nextDay = approval.getExpiresAt();
        try {
            nextDay = DateUtils.getNextDays(new Date(), (authProperties.getApproval_Time() == 0 ? 30 : authProperties.getApproval_Time()));
        } catch (Exception e) {
            log.warn("approval时间转换参数为:{},错误:{}", authProperties.getApproval_Time(), e.getMessage());
        }
        oauthApproval.setExpireAt(nextDay.getTime());
        oauthApproval.setLastModifiedAt(approval.getLastUpdatedAt().getTime());
        oauthApproval.setStatus(approval.getStatus() != null ? approval.getStatus() : Approval.ApprovalStatus.APPROVED);
        try {
            oauthApprovalDao.insert(oauthApproval);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean revokeApprovals(Collection<Approval> approvals) {
        boolean isSuccess = true;
        Iterator<Approval> iterator = approvals.iterator();

        while (iterator.hasNext()) {
            Approval approval = iterator.next();
            if (handleRevocationsAsExpiry) {
                Query query = new Query();
                query.addCriteria(Criteria.where(OauthApproval.USER_ID).is(approval.getUserId()));
                query.addCriteria(Criteria.where(OauthApproval.CLIENT_ID).is(approval.getClientId()));
                query.addCriteria(Criteria.where(OauthApproval.SCOPE).is(approval.getScope()));

                Update update = new Update();
                update.set(OauthApproval.EXPIRE_AT, System.currentTimeMillis());

                long updateCount = oauthApprovalDao.update(query, update);
                isSuccess = (updateCount == 1);
            } else {
                Query query = new Query();
                query.addCriteria(Criteria.where(OauthApproval.USER_ID).is(approval.getUserId()));
                query.addCriteria(Criteria.where(OauthApproval.CLIENT_ID).is(approval.getClientId()));
                query.addCriteria(Criteria.where(OauthApproval.SCOPE).is(approval.getScope()));

                long deleteCount = oauthApprovalDao.delete(query);
                isSuccess = (deleteCount == 1);
            }
        }
        return isSuccess;
    }

    @Override
    public Collection<Approval> getApprovals(String username, String clientId) {
        Collection<Approval> approvals = new ArrayList<Approval>();

        Query query = new Query();
        query.addCriteria(Criteria.where(OauthApproval.CLIENT_ID).is(clientId));
        query.addCriteria(Criteria.where(OauthApproval.USER_ID).is(username));

        List<OauthApproval> oauthApprovals = oauthApprovalDao.find(query);

        for (OauthApproval oauthApproval : oauthApprovals) {
            approvals.add(new Approval(oauthApproval.getUserId(), oauthApproval.getClientId(),
                    oauthApproval.getScope(), new Date(oauthApproval.getExpireAt()), oauthApproval.getStatus(),
                    new Date(oauthApproval.getLastModifiedAt())));
        }

        return approvals;
    }

    public boolean isHandleRevocationsAsExpiry() {
        return handleRevocationsAsExpiry;
    }

    public void setHandleRevocationsAsExpiry(boolean handleRevocationsAsExpiry) {
        this.handleRevocationsAsExpiry = handleRevocationsAsExpiry;
    }
}
