package com.fjm.service.oauth;

import com.fjm.dao.IOauthClientDao;
import com.fjm.emun.DeleteEnum;
import com.fjm.emun.StatusEnum;
import com.fjm.entity.OauthClient;
import com.fjm.utils.Constants;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.provider.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-17 下午4:27
 * @Description:
 */
public class OauthClientService implements ClientDetailsService, ClientRegistrationService {

    @Resource
    private IOauthClientDao oauthClientDao;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                .addCriteria(Criteria.where(Constants.STATUS).is(StatusEnum.ACTIVE_STATUS.getValue()))
                .addCriteria(Criteria.where(OauthClient.CLIENT_ID).is(clientId));
        OauthClient clientDetails = oauthClientDao.findOne(query);
        if (clientDetails == null) {
            throw new ClientRegistrationException(String.format("Client with id %s not found", clientId));
        }
        return clientDetails;
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        ClientDetails queryClient = null;
        try {
            queryClient = loadClientByClientId(clientDetails.getClientId());
        } catch (Exception e) {
        }
        if (queryClient == null) {
            OauthClient oauthClient =
                    new OauthClient(clientDetails.getClientId(), clientDetails.getResourceIds(),
                            clientDetails.isSecretRequired(), NoOpPasswordEncoder.getInstance().encode(clientDetails.getClientSecret()),
                            clientDetails.isScoped(),
                            clientDetails.getScope(), clientDetails.getAuthorizedGrantTypes(), clientDetails.getRegisteredRedirectUri(),
                            clientDetails.getAuthorities(), clientDetails.getAccessTokenValiditySeconds(),
                            clientDetails.getRefreshTokenValiditySeconds(), clientDetails.isAutoApprove("true"),
                            clientDetails.getAdditionalInformation(), System.currentTimeMillis());
            oauthClientDao.insert(oauthClient);
        } else {
            throw new ClientAlreadyExistsException(String.format("Client with id %s already existed",
                    clientDetails.getClientId()));
        }
    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                .addCriteria(Criteria.where(OauthClient.CLIENT_ID).is(clientDetails.getClientId()));

        Update update = new Update();
        update.set(OauthClient.RESOURCE_IDS, clientDetails.getResourceIds());
        update.set(OauthClient.SCOPE, clientDetails.getScope());
        update.set(OauthClient.AUTHORIZED_GRANT_TYPES, clientDetails.getAuthorizedGrantTypes());
        update.set(OauthClient.REGISTERED_REDIRECT_URI, clientDetails.getRegisteredRedirectUri());
        update.set(OauthClient.AUTHORITIES, clientDetails.getAuthorities());
        update.set(OauthClient.ACCESS_TOKEN_VALIDITY_SECONDS, clientDetails.getAccessTokenValiditySeconds());
        update.set(OauthClient.REFRESH_TOKEN_VALIDITY_SECONDS, clientDetails.getRefreshTokenValiditySeconds());
        update.set(OauthClient.ADDITIONAL_INFORMATION, clientDetails.getAdditionalInformation());

        long updateCount = oauthClientDao.update(query, update);
        if (updateCount <= 0) {
            throw new NoSuchClientException(String.format("Client with id %s not found", clientDetails.getClientId()));
        }
    }

    @Override
    public void updateClientSecret(String clientId, String clientSecret) throws NoSuchClientException {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                .addCriteria(Criteria.where(OauthClient.CLIENT_ID).is(clientId));

        Update update = new Update();
        //todo 替换升级加密clientSercret方法
        update.set(OauthClient.CLIENT_SECRET, NoOpPasswordEncoder.getInstance().encode(clientSecret));

        long updateCount = oauthClientDao.update(query, update);

        if (updateCount <= 0) {
            throw new NoSuchClientException(String.format("Client with id %s not found", clientId));
        }
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()))
                .addCriteria(Criteria.where(OauthClient.CLIENT_ID).is(clientId));

        long deleteCount = oauthClientDao.delete(query);

        if (deleteCount <= 0) {
            throw new NoSuchClientException(String.format("Client with id %s not found", clientId));
        }
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DELETED).is(DeleteEnum.NO_DELETED.getValue()));
        List<ClientDetails> result = new ArrayList<ClientDetails>();
        List<OauthClient> details = oauthClientDao.find(query);
        for (OauthClient detail : details) {
            result.add(detail);
        }
        return result;
    }
}
