package io.uniflow.core.flow

import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.UniFlowLogger


class UIDataStore(private val publisher: UIDataPublisher, defaultState: UIState) {

    var currentState: UIState = defaultState
        private set

    suspend fun pushNewData(dataUpdate: UIDataUpdate) {
        UniFlowLogger.debug("UIDataStore - push: $dataUpdate")
        when (dataUpdate.data) {
            is UIState -> {
                currentState = dataUpdate.data
                UniFlowLogger.logState(dataUpdate.data)
                when (dataUpdate.type) {
                    UIDataUpdateType.PUBLISH -> publisher.publishState(dataUpdate.data)
                    else -> {
                        UniFlowLogger.log("[State] internal update '${dataUpdate.data}'")
                    }
                }
            }
            is UIEvent -> {
                UniFlowLogger.logEvent(dataUpdate.data)
                publisher.publishEvent(dataUpdate.data)
            }
        }
    }
}