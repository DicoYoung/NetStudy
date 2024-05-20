package com.netstudy.ucenter.service;

import com.netstudy.ucenter.model.dto.FindPswDto;
import com.netstudy.ucenter.model.dto.RegisterDto;

public interface VerifyService {
    void findPassword(FindPswDto findPswDto);

    void register(RegisterDto registerDto);
}
