package com.kfokam.kos.service;

import com.kfokam.kos.dto.TableauResponse;
import java.util.List;

public interface TableauService {

    List<TableauResponse> construire(Long promotionId);
}
