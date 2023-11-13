package systems.ajax.infrastructure.nats.mapper

import systems.ajax.domain.model.Library
import systems.ajax.entity.Library as ProtoLibrary

fun Library.toProto(): ProtoLibrary =
    ProtoLibrary.newBuilder()
        .setId(id)
        .setName(name)
        .setOwnerId(ownerId)
        .build()

fun ProtoLibrary.toModel(): Library =
    Library(id, name, ownerId)
